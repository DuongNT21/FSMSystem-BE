package com.swp391_be.SWP391_be.controller;

import org.springframework.web.bind.annotation.RestController;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.swp391_be.SWP391_be.service.IBouquetService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  @PostMapping("/create")
  public Bouquet create(@RequestBody CreateBouquetRequest request) {
    return bouquetService.createBouquet(request);
  }

  @PutMapping("/update")
  public Bouquet update(@RequestBody UpdateBouquetRequest request) {
    return bouquetService.updateBouquet(request);
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
