package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.CreateRawMaterialBatchRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.UpdateBatchRequest;
import com.swp391_be.SWP391_be.dto.response.inventoryLogResponse.InventoryLogResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterialBatch.RawMaterialBatchResponse;

import java.util.List;

public interface IRawMaterialBatchService {
    RawMaterialBatchResponse createBatch(CreateRawMaterialBatchRequest request);

    RawMaterialBatchResponse updateBatch(int id, UpdateBatchRequest request);

    List<RawMaterialBatchResponse> getAll();

    List<InventoryLogResponse> getLogsByBatch(int batchId);
}
