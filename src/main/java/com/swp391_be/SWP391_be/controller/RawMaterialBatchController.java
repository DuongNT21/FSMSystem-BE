package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.CreateRawMaterialBatchRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.UpdateBatchRequest;
import com.swp391_be.SWP391_be.dto.response.inventoryLogResponse.InventoryLogResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterialBatch.RawMaterialBatchResponse;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.service.IRawMaterialBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.API + "/raw-material-batches")
public class RawMaterialBatchController {

    private final IRawMaterialBatchService rawMaterialBatchService;

    @PostMapping
    public ResponseEntity<BaseResponse<RawMaterialBatchResponse>> createBatch(
            @RequestBody CreateRawMaterialBatchRequest request) {

        RawMaterialBatchResponse result = rawMaterialBatchService.createBatch(request);

        BaseResponse<RawMaterialBatchResponse> response = BaseResponse.<RawMaterialBatchResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Create batch successfully")
                .data(result)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RawMaterialBatchResponse>> updateBatch(
            @PathVariable int id,
            @RequestBody UpdateBatchRequest request) {

        RawMaterialBatchResponse result = rawMaterialBatchService.updateBatch(id, request);

        BaseResponse<RawMaterialBatchResponse> response = BaseResponse.<RawMaterialBatchResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Update batch successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RawMaterialBatchResponse>>> getAll() {

        List<RawMaterialBatchResponse> result = rawMaterialBatchService.getAll();

        BaseResponse<List<RawMaterialBatchResponse>> response = BaseResponse.<List<RawMaterialBatchResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get all batches successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<BaseResponse<List<InventoryLogResponse>>> getLogsByBatch(
            @PathVariable int id) {

        List<InventoryLogResponse> result = rawMaterialBatchService.getLogsByBatch(id);

        BaseResponse<List<InventoryLogResponse>> response = BaseResponse.<List<InventoryLogResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get logs successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }
}