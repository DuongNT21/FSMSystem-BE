package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.bouquet.CreateBouquetResponse;
import com.swp391_be.SWP391_be.service.IBouquetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bouquet")
@RequiredArgsConstructor
public class BouquetController {
    private final IBouquetService bouquetService;
    @PostMapping("/create")
    public ResponseEntity<BaseResponse<CreateBouquetResponse>> createBouquet(@RequestBody CreateBouquetRequest request) {
        CreateBouquetResponse response = bouquetService.createBouquet(request);
        BaseResponse<CreateBouquetResponse> baseResponse = new BaseResponse<CreateBouquetResponse>();
        baseResponse.setStatus(HttpStatus.CREATED.value());
        baseResponse.setMessage("Create Bouquet");
        baseResponse.setData(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }
}
