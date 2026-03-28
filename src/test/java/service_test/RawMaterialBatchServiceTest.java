package service_test;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RawMaterialBatchServiceTest {

    @Mock
    private RawMaterialBatchRepository batchRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    @InjectMocks
    private RawMaterialBatchService service;

    // ================= CREATE BATCH =================

    @Test
    void createBatch_success() {
        CreateRawMaterialBatchRequest request = new CreateRawMaterialBatchRequest();
        request.setRawMaterialId(1);
        request.setOriginalQuantity(10);
        request.setImportPrice(100.0f);
        request.setImportDate(new Date());
        request.setExpireDate(new Date(System.currentTimeMillis() + 1000000));

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setId(1);
        rawMaterial.setName("Rose");

        when(rawMaterialRepository.getById(1)).thenReturn(rawMaterial);

        RawMaterialBatches savedBatch = new RawMaterialBatches();
        savedBatch.setId(1);
        savedBatch.setRawMaterial(rawMaterial);
        savedBatch.setRemainQuantity(10);
        savedBatch.setOriginalQuantity(10);
        savedBatch.setExpireDate(request.getExpireDate());
        savedBatch.setImportDate(request.getImportDate());

        when(batchRepository.save(any())).thenReturn(savedBatch);

        var response = service.createBatch(request);

        assertNotNull(response);
        assertEquals(10, response.getRemainQuantity());

        verify(batchRepository).save(any());
        verify(inventoryLogRepository).save(any(InventoryLogs.class));
    }

    // ================= UPDATE BATCH =================

    @Test
    void updateBatch_success_updateExpireDate() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setId(1);
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();
        request.setExpireDate(new Date(System.currentTimeMillis() + 100000));

        var response = service.updateBatch(1, request);

        assertNotNull(response);
        verify(batchRepository).save(batch);
        verify(inventoryLogRepository).save(any());
    }

    @Test
    void updateBatch_fail_batchNotFound() {
        when(batchRepository.findById(1)).thenReturn(Optional.empty());

        UpdateBatchRequest request = new UpdateBatchRequest();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));

        assertEquals("Batch not found", ex.getMessage());
    }

    @Test
    void updateBatch_fail_invalidExpireDate() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();
        request.setExpireDate(new Date(batch.getImportDate().getTime() - 10000));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));

        assertEquals("Expire date cannot be before import date", ex.getMessage());
    }

    @Test
    void updateBatch_fail_negativePrice() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();
        request.setImportPrice(-10.0f);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));

        assertEquals("Import price cannot be negative", ex.getMessage());
    }

    @Test
    void updateBatch_fail_noFieldsUpdated() {
        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setImportDate(new Date());

        when(batchRepository.findById(1)).thenReturn(Optional.of(batch));

        UpdateBatchRequest request = new UpdateBatchRequest();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateBatch(1, request));

        assertEquals("No valid fields provided for update", ex.getMessage());
    }

    // ================= GET ALL =================

    @Test
    void getAll_success() {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setId(1);
        rawMaterial.setName("Rose");

        RawMaterialBatches batch = new RawMaterialBatches();
        batch.setId(1);
        batch.setRawMaterial(rawMaterial);
        batch.setRemainQuantity(5);
        batch.setOriginalQuantity(10);
        batch.setImportDate(new Date());
        batch.setExpireDate(new Date(System.currentTimeMillis() + 1000000));

        when(batchRepository.findAll()).thenReturn(List.of(batch));

        var result = service.getAll();

        assertEquals(1, result.size());
    }

    // ================= GET LOG =================

    @Test
    void getLogsByBatch_success() {
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
    void getLogsByBatch_fail_notFound() {
        when(batchRepository.existsById(1)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getLogsByBatch(1));

        assertEquals("Batch not found", ex.getMessage());
    }
}
