package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.review.CreateReviewRequest;
import com.swp391_be.SWP391_be.dto.response.review.CreateReviewResponse;
import com.swp391_be.SWP391_be.dto.response.review.GetReviewResponse;
import com.swp391_be.SWP391_be.service.IReviewService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetCostResponse;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.exception.GlobalException;

import lombok.RequiredArgsConstructor;

import com.swp391_be.SWP391_be.service.IBouquetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/bouquets")
@RequiredArgsConstructor
public class BouquetController {
  private final IBouquetService bouquetService;
  private final IReviewService reviewService;

  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<Bouquet>> create(
      @RequestPart("request") String requestJson,
      @RequestPart("images") List<MultipartFile> images) throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    CreateBouquetRequest request = mapper.readValue(requestJson, CreateBouquetRequest.class);

    Bouquet response = bouquetService.createBouquet(request, images);
    BaseResponse<Bouquet> baseResponse = new BaseResponse<>();
    baseResponse.setStatus(HttpStatus.CREATED.value());
    baseResponse.setMessage("Create Bouquet");
    baseResponse.setData(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
  }

  @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<Bouquet>> update(
      @RequestPart("request") String requestJson,
      @RequestPart("id") String id,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    UpdateBouquetRequest request = mapper.readValue(requestJson, UpdateBouquetRequest.class);
    int idInt = Integer.parseInt(id);
    request.setId(idInt);
    Bouquet response = bouquetService.updateBouquet(request, images);
    BaseResponse<Bouquet> baseResponse = new BaseResponse<>();
    baseResponse.setStatus(HttpStatus.CREATED.value());
    baseResponse.setMessage("Update Bouquet");
    baseResponse.setData(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
  }

  @GetMapping("/get")
  public PageResponse<BouquetListResponse> getBouquet(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      @ModelAttribute GetBouquetCriteriaRequest request) {
    
    Direction direction = sortOrder.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
    Sort sortObj = Sort.by(direction, sortBy);
    Pageable pageable = PageRequest.of(page, size, sortObj);
    return bouquetService.getBouquets(pageable, request);
  }

  @Operation(summary = "Get bouquet by id")
  @GetMapping("/getById")
  public Bouquet getById(@RequestParam int id) {
    return bouquetService.getById(id);
  }

  @Operation(summary = "Delete bouquet by id")
  @DeleteMapping("/delete")
  public void delete(@RequestParam int id) {
    bouquetService.deleteBouquet(id);
  }

  @Operation(summary = "Get unit cost of a raw material based on latest active batch")
  @GetMapping("/cost")
  public ResponseEntity<BaseResponse<Float>> getMaterialCost(@RequestParam int materialId) {
    float cost = bouquetService.getMaterialCost(materialId);
    BaseResponse<Float> response = new BaseResponse<>();
    response.setStatus(HttpStatus.OK.value());
    response.setMessage("Unit material cost");
    response.setData(cost);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/review")
  public ResponseEntity<BaseResponse<CreateReviewResponse>> createReview(@PathVariable int id,
      @RequestBody CreateReviewRequest createReviewRequest) {
    CreateReviewResponse createReviewResponse = reviewService.createReview(id, createReviewRequest);
    BaseResponse<CreateReviewResponse> baseResponse = new BaseResponse<>();
    baseResponse.setData(createReviewResponse);
    baseResponse.setMessage("Create review successfully");
    baseResponse.setStatus(HttpStatus.CREATED.value());
    return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
  }

  @GetMapping("/{id}/review")
  public ResponseEntity<BaseResponse<PageResponse<GetReviewResponse>>> getReviews(
      @PathVariable int id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "desc") String sort) {

    PageResponse<GetReviewResponse> data = reviewService.getAllReviews(id, page, size, sort);

    BaseResponse<PageResponse<GetReviewResponse>> response = new BaseResponse<>();
    response.setStatus(HttpStatus.OK.value());
    response.setMessage("Get review list successfully");
    response.setData(data);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/trending-today")
  public ResponseEntity<BaseResponse<List<Bouquet>>> getMostRatedToday() {
    List<Bouquet> bouquets = bouquetService.getMostRatedBouquetsToday();
    BaseResponse<List<Bouquet>> response = new BaseResponse<>();
    response.setData(bouquets);
    response.setMessage("Most rated bouquet today");
    response.setStatus(HttpStatus.OK.value());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/top-rated")
  public ResponseEntity<BaseResponse<List<Bouquet>>> getTopRated() {
    List<Bouquet> bouquets = bouquetService.getTop4RatedBouquets();
    BaseResponse<List<Bouquet>> response = new BaseResponse<>();
    response.setData(bouquets);
    response.setMessage("Top 4 rated bouquets");
    response.setStatus(HttpStatus.OK.value());
    return ResponseEntity.ok(response);
  }
}
