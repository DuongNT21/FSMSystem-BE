package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.CreateRawMaterialBatchRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.UpdateBatchRequest;
import com.swp391_be.SWP391_be.dto.response.inventoryLogResponse.InventoryLogResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterialBatch.RawMaterialBatchResponse;
import com.swp391_be.SWP391_be.entity.InventoryLogs;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.entity.RawMaterialBatches;
import com.swp391_be.SWP391_be.enums.EActionType;
import com.swp391_be.SWP391_be.repository.InventoryLogRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialBatchRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialRepository;
import com.swp391_be.SWP391_be.service.IRawMaterialBatchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialBatchService implements IRawMaterialBatchService {

    private final RawMaterialBatchRepository rawMaterialBatchRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final InventoryLogRepository inventoryLogRepository;

    @Override
    @Transactional
    public RawMaterialBatchResponse createBatch(CreateRawMaterialBatchRequest request) {

        RawMaterial rawMaterial = rawMaterialRepository.getById(request.getRawMaterialId());

        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setRawMaterial(rawMaterial);
        batch.setImportDate(request.getImportDate());
        batch.setExpireDate(request.getExpireDate());
        batch.setImportPrice(request.getImportPrice());
        batch.setOriginalQuantity(request.getOriginalQuantity());
        batch.setRemainQuantity(request.getOriginalQuantity());

        rawMaterialBatchRepository.save(batch);

        InventoryLogs log = new InventoryLogs();
        log.setRawMaterialBatches(batch);
        log.setActionType(EActionType.Import);
        log.setQuantity(request.getOriginalQuantity());
        log.setCreatedAt(LocalDateTime.now());

        inventoryLogRepository.save(log);

        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public RawMaterialBatchResponse updateBatch(int id, UpdateBatchRequest request) {

        RawMaterialBatches batch = rawMaterialBatchRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        boolean isUpdated = false;

        if (request.getExpireDate() != null) {
            batch.setExpireDate(request.getExpireDate());
            isUpdated = true;
        }

        if (request.getImportPrice() > 0) {
            batch.setImportPrice(request.getImportPrice());
            isUpdated = true;
        }

        rawMaterialBatchRepository.save(batch);

        if (isUpdated) {
            InventoryLogs log = new InventoryLogs();
            log.setRawMaterialBatches(batch);
            log.setActionType(EActionType.Adjust);
            log.setQuantity(0);
            log.setCreatedAt(LocalDateTime.now());
            inventoryLogRepository.save(log);
        }

        return mapToResponse(batch);
    }

    @Override
    public List<RawMaterialBatchResponse> getAll() {

        List<RawMaterialBatches> batches = rawMaterialBatchRepository.findAll();

        return batches.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<InventoryLogResponse> getLogsByBatch(int batchId) {

        if (!rawMaterialBatchRepository.existsById(batchId)) {
            throw new RuntimeException("Batch not found");
        }

        List<InventoryLogs> logs =
                inventoryLogRepository.findByRawMaterialBatchesId(batchId);

        return logs.stream().map(log -> {

            InventoryLogResponse response = new InventoryLogResponse();
            response.setId(log.getId());
            response.setActionType(log.getActionType());
            response.setQuantity(log.getQuantity());
            response.setCreatedAt(log.getCreatedAt());

            return response;

        }).toList();
    }

    private RawMaterialBatchResponse mapToResponse(RawMaterialBatches batch) {

        RawMaterialBatchResponse response = new RawMaterialBatchResponse();

        response.setId(batch.getId());
        response.setImportDate(batch.getImportDate());
        response.setRawMaterialName(batch.getRawMaterial().getName());
        response.setExpireDate(batch.getExpireDate());
        response.setImportPrice(batch.getImportPrice());
        response.setOriginalQuantity(batch.getOriginalQuantity());
        response.setRemainQuantity(batch.getRemainQuantity());

        if (batch.getRawMaterial() != null) {
            response.setRawMaterialId(batch.getRawMaterial().getId());
            response.setRawMaterialName(batch.getRawMaterial().getName());
        }

        return response;
    }
}