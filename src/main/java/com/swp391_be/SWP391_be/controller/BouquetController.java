package com.swp391_be.SWP391_be.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.exception.GlobalException;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.swp391_be.SWP391_be.service.IBouquetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/bouquets")
@RequiredArgsConstructor
public class BouquetController {
  private final IBouquetService bouquetService;

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
      @RequestParam(defaultValue = "createdAt,asc") String sort,
      @ModelAttribute GetBouquetCriteriaRequest request) {
    Sort sortObj = Sort.by(
        sort.split(",")[1].equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC,
        sort.split(",")[0]);
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

}
