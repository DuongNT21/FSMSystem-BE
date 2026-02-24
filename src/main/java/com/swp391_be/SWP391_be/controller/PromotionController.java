package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.dto.request.promotion.CreatePromotionRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.promotion.PromotionResponse;
import com.swp391_be.SWP391_be.entity.Promotion;
import com.swp391_be.SWP391_be.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final IPromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<BaseResponse<PromotionResponse>> createPromotion(@RequestBody CreatePromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        BaseResponse<PromotionResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(response);
        baseResponse.setMessage("Create promotion successfully");
        baseResponse.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('Admin', 'Staff')")
    public ResponseEntity<BaseResponse<PageResponse<PromotionResponse>>> getAllPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String name) {

        String[] sortArr = sort.split(",");
        Sort.Direction direction = sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortArr[0]));

        PageResponse<PromotionResponse> response = promotionService.getAllPromotions(name, pageable);
        BaseResponse<PageResponse<PromotionResponse>> baseResponse = new BaseResponse<>();
        baseResponse.setData(response);
        baseResponse.setMessage("Get promotions successfully");
        baseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyAuthority('Admin', 'Staff')")
    public ResponseEntity<BaseResponse<PromotionResponse>> checkPromotion(@RequestParam String code, @RequestParam double orderValue) {
        PromotionResponse response = promotionService.getPromotionByCode(code, orderValue);
        BaseResponse<PromotionResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(response);
        baseResponse.setMessage("Promotion is valid");
        baseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(baseResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<BaseResponse<Void>> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        BaseResponse<Void> baseResponse = new BaseResponse<>();
        baseResponse.setMessage("Delete promotion successfully");
        baseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(baseResponse);
    }
}
