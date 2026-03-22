package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.CreateRawMaterialBatchRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterialBatch.UpdateBatchRequest;
import com.swp391_be.SWP391_be.entity.InventoryLogs;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.entity.RawMaterialBatches;
import com.swp391_be.SWP391_be.enums.EBatchStatus;
import com.swp391_be.SWP391_be.repository.InventoryLogRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialBatchRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialRepository;
import com.swp391_be.SWP391_be.service.impl.RawMaterialBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RawMaterialBatchServiceTest {

    @Mock
    private RawMaterialBatchRepository batchRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    private RawMaterialBatchService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new RawMaterialBatchService(
                batchRepository,
                rawMaterialRepository,
                inventoryLogRepository
        );
    }

    // ================= CREATE =================

    @Test
    void createBatch_success() {
        CreateRawMaterialBatchRequest request = new CreateRawMaterialBatchRequest();
        request.setRawMaterialId(1);
        request.setOriginalQuantity(10);
        request.setImportPrice(100.0f);
        request.setImportDate(new Date());
        request.setExpireDate(new Date(System.currentTimeMillis() + 100000));

        RawMaterial raw = new RawMaterial();
        raw.setId(1);
        raw.setName("Rose");

        when(rawMaterialRepository.getById(1)).thenReturn(raw);

        RawMaterialBatches saved = new RawMaterialBatches();
        saved.setId(1);
        saved.setRawMaterial(raw);
        saved.setRemainQuantity(10);
        saved.setOriginalQuantity(10);
        saved.setImportDate(request.getImportDate());
        saved.setExpireDate(request.getExpireDate());

        when(batchRepository.save(any())).thenReturn(saved);

        var result = service.createBatch(request);

        assertNotNull(result);
        assertEquals(10, result.getRemainQuantity());

        verify(batchRepository).save(any());
        verify(inventoryLogRepository).save(any(InventoryLogs.class));
    }

    @Test
    void createBatch_fail_rawMaterialNotFound() {
        when(rawMaterialRepository.getById(1))
                .thenThrow(new RuntimeException());

        CreateRawMaterialBatchRequest request = new CreateRawMaterialBatchRequest();
        request.setRawMaterialId(1);

        assertThrows(RuntimeException.class,
                () -> service.createBatch(request));
    }

    @Test
    void updateBatch_fail_notFound() {
        when(batchRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, new UpdateBatchRequest()));
    }

    @Test
    void updateBatch_fail_invalidExpireDate() {
        RawMaterialBatches batch = new RawMaterialBatches();
        Date importDate = new Date();
        batch.setImportDate(importDate);

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();
        request.setExpireDate(new Date(importDate.getTime() - 1));

        assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));
    }

    @Test
    void updateBatch_fail_negativePrice() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();
        request.setImportPrice(-10.0f);

        assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));
    }

    @Test
    void updateBatch_fail_noFieldProvided() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, new UpdateBatchRequest()));
    }

    // ================= GET ALL =================

    @Test
    void getAll_success() {
        RawMaterial raw = new RawMaterial();
        raw.setId(1);
        raw.setName("Rose");

        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setRawMaterial(raw);
        batch.setRemainQuantity(5);
        batch.setOriginalQuantity(10);
        batch.setImportDate(new Date());
        batch.setExpireDate(new Date(System.currentTimeMillis() + 100000));

        when(batchRepository.findAll()).thenReturn(List.of(batch));

        var result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Rose", result.get(0).getRawMaterialName());
    }

    // ================= STATUS EDGE CASE =================

    @Test
    void getAll_status_outOfStock() {
        RawMaterial raw = new RawMaterial();
        raw.setId(1);
        raw.setName("Rose");

        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setRawMaterial(raw);
        batch.setRemainQuantity(0);
        batch.setOriginalQuantity(10);
        batch.setImportDate(new Date());
        batch.setExpireDate(new Date(System.currentTimeMillis() + 100000));

        when(batchRepository.findAll()).thenReturn(List.of(batch));

        var result = service.getAll();

        assertEquals(EBatchStatus.OUT_OF_STOCK, result.get(0).getStatus());
    }

    // ================= LOG =================

    @Test
    void getLogs_success() {
        when(batchRepository.existsById(1)).thenReturn(true);

        InventoryLogs log = new InventoryLogs();
        log.setId(1);
        log.setQuantity(10);

        when(inventoryLogRepository.findByRawMaterialBatchesId(1))
                .thenReturn(List.of(log));

        var result = service.getLogsByBatch(1);

        assertEquals(1, result.size());
    }

    @Test
    void getLogs_fail_notFound() {
        when(batchRepository.existsById(1)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.getLogsByBatch(1));
    }
}