package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.MaterialReq;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.entity.*;
import com.swp391_be.SWP391_be.enums.EBatchStatus;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.repository.*;
import com.swp391_be.SWP391_be.service.impl.BouquetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BouquetServiceTest {

    @Mock private BouquetRepository bouquetRepository;
    @Mock private RawMaterialRepository rawMaterialrepository;
    @Mock private RawMaterialBatchRepository rawMaterialBatchRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private IImageService imageService;

    @InjectMocks
    private BouquetService bouquetService;

    private RawMaterial rawMaterial;
    private Bouquet existingBouquet;

    @BeforeEach
    void setUp() {
        rawMaterial = new RawMaterial();
        rawMaterial.setId(1);
        rawMaterial.setName("Rose");
        rawMaterial.setTotalQuantity(50);

        existingBouquet = new Bouquet();
        existingBouquet.setId(1);
        existingBouquet.setName("Spring Bouquet");
        existingBouquet.setPrice(100_000f);
        existingBouquet.setStatus(1);
        existingBouquet.setDescription("A lovely bouquet");
    }


    private CreateBouquetRequest validCreateRequest() {
        CreateBouquetRequest req = new CreateBouquetRequest();
        req.setName("Summer Bouquet");
        req.setPrice(150_000f);
        req.setStatus(1);
        req.setDescription("Fresh flowers");
        return req;
    }

    private RawMaterialBatches activeBatch(int rawMaterialId, float importPrice, int originalQty, int remainQty) {
        RawMaterialBatches batch = new RawMaterialBatches();
        RawMaterial rm = new RawMaterial();
        rm.setId(rawMaterialId);
        batch.setRawMaterial(rm);
        batch.setImportPrice(importPrice);
        batch.setOriginalQuantity(originalQty);
        batch.setRemainQuantity(remainQty);
        batch.setStatus(EBatchStatus.ACTIVE);
        batch.setImportDate(new Date());
        return batch;
    }


    @Test
    void createBouquet_success_noMaterials_noImages() throws IOException {
        CreateBouquetRequest req = validCreateRequest();
        when(bouquetRepository.existsByName("Summer Bouquet")).thenReturn(false);
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bouquet result = bouquetService.createBouquet(req, Collections.emptyList());

        assertThat(result.getName()).isEqualTo("Summer Bouquet");
        assertThat(result.getPrice()).isEqualTo(150_000f);
        assertThat(result.getStatus()).isEqualTo(1);
        verify(bouquetRepository).save(any(Bouquet.class));
    }

    @Test
    void createBouquet_success_withMaterials() throws IOException {
        CreateBouquetRequest req = validCreateRequest();
        req.setMaterials(List.of(new MaterialReq(1, 3)));

        when(bouquetRepository.existsByName("Summer Bouquet")).thenReturn(false);
        when(rawMaterialrepository.findById(1)).thenReturn(Optional.of(rawMaterial));
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bouquet result = bouquetService.createBouquet(req, Collections.emptyList());

        assertThat(result.getBouquetsMaterials()).hasSize(1);
        assertThat(result.getBouquetsMaterials().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void createBouquet_success_withCategoryAndImages() throws IOException {
        CreateBouquetRequest req = validCreateRequest();
        req.setCategoryId(2);

        Category category = new Category();
        category.setId(2);

        MultipartFile file = mock(MultipartFile.class);
        when(bouquetRepository.existsByName("Summer Bouquet")).thenReturn(false);
        when(categoryRepository.findById(any(Integer.class))).thenReturn(Optional.of(category));
        when(imageService.uploadImage(file)).thenReturn(Map.of("url", "http://img.url", "publicId", "pub123"));
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bouquet result = bouquetService.createBouquet(req, List.of(file));

        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImage()).isEqualTo("http://img.url");
    }

    @Test
    void createBouquet_throwsWhenNameIsNull() {
        CreateBouquetRequest req = validCreateRequest();
        req.setName(null);

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet name is required");
    }

    @Test
    void createBouquet_throwsWhenNameIsEmpty() {
        CreateBouquetRequest req = validCreateRequest();
        req.setName("");

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet name is required");
    }

    @Test
    void createBouquet_throwsWhenNameAlreadyExists() {
        CreateBouquetRequest req = validCreateRequest();
        when(bouquetRepository.existsByName("Summer Bouquet")).thenReturn(true);

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet name already exists");
    }

    @Test
    void createBouquet_throwsWhenPriceIsZero() {
        CreateBouquetRequest req = validCreateRequest();
        req.setPrice(0);
        when(bouquetRepository.existsByName(any())).thenReturn(false);

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet price must be greater than 0");
    }

    @Test
    void createBouquet_throwsWhenPriceIsNegative() {
        CreateBouquetRequest req = validCreateRequest();
        req.setPrice(-1f);
        when(bouquetRepository.existsByName(any())).thenReturn(false);

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet price must be greater than 0");
    }

    @Test
    void createBouquet_throwsWhenStatusIsInvalid() {
        CreateBouquetRequest req = validCreateRequest();
        req.setStatus(5);
        when(bouquetRepository.existsByName(any())).thenReturn(false);

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet status must be 0 or 1");
    }

    @Test
    void createBouquet_throwsWhenNotEnoughMaterial() {
        CreateBouquetRequest req = validCreateRequest();
        req.setMaterials(List.of(new MaterialReq(1, 100)));

        rawMaterial.setTotalQuantity(10);
        when(bouquetRepository.existsByName(any())).thenReturn(false);
        when(rawMaterialrepository.findById(1)).thenReturn(Optional.of(rawMaterial));

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Not enough raw material in stock");
    }

    @Test
    void createBouquet_throwsWhenMaterialQuantityIsZero() {
        CreateBouquetRequest req = validCreateRequest();
        req.setMaterials(List.of(new MaterialReq(1, 0)));

        when(bouquetRepository.existsByName(any())).thenReturn(false);
        when(rawMaterialrepository.findById(1)).thenReturn(Optional.of(rawMaterial));

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Material quantity must be greater than 0");
    }

    @Test
    void createBouquet_throwsWhenRawMaterialNotFound() {
        CreateBouquetRequest req = validCreateRequest();
        req.setMaterials(List.of(new MaterialReq(99, 2)));

        when(bouquetRepository.existsByName(any())).thenReturn(false);
        when(rawMaterialrepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bouquetService.createBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Raw material not found");
    }

    @Test
    void createBouquet_throwsWhenImageUploadFails() throws IOException {
        CreateBouquetRequest req = validCreateRequest();
        MultipartFile file = mock(MultipartFile.class);

        when(bouquetRepository.existsByName(any())).thenReturn(false);
        when(imageService.uploadImage(file)).thenThrow(new IOException("upload failed"));

        assertThatThrownBy(() -> bouquetService.createBouquet(req, List.of(file)))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Image upload failed");
    }

    // ─── getById ───────────────────────────────────────────────────────────────

    @Test
    void getById_success() {
        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));

        Bouquet result = bouquetService.getById(1);

        assertThat(result).isEqualTo(existingBouquet);
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(bouquetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bouquetService.getById(99))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet not found");
    }

    // ─── deleteBouquet ─────────────────────────────────────────────────────────

    @Test
    void deleteBouquet_success() {
        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));

        bouquetService.deleteBouquet(1);

        verify(bouquetRepository).delete(existingBouquet);
    }

    @Test
    void deleteBouquet_throwsWhenNotFound() {
        when(bouquetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bouquetService.deleteBouquet(99))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet not found");
    }

    // ─── updateBouquet ─────────────────────────────────────────────────────────

    @Test
    void updateBouquet_success() throws IOException {
        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(1);
        req.setName("Spring Bouquet");
        req.setPrice(120_000f);
        req.setStatus(1);
        req.setDescription("Updated");

        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));
        when(bouquetRepository.existsByName("Spring Bouquet")).thenReturn(true);
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Bouquet result = bouquetService.updateBouquet(req, Collections.emptyList());

        assertThat(result.getPrice()).isEqualTo(120_000f);
        assertThat(result.getDescription()).isEqualTo("Updated");
    }

    @Test
    void updateBouquet_throwsWhenBouquetNotFound() {
        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(99);
        req.setName("X");
        req.setPrice(100f);
        req.setStatus(1);

        when(bouquetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bouquetService.updateBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet not found");
    }

    @Test
    void updateBouquet_throwsWhenNameAlreadyTakenByOther() {
        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(1);
        req.setName("Other Bouquet");
        req.setPrice(100_000f);
        req.setStatus(1);

        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));
        when(bouquetRepository.existsByName("Other Bouquet")).thenReturn(true);

        assertThatThrownBy(() -> bouquetService.updateBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet name already exists");
    }

    @Test
    void updateBouquet_throwsWhenPriceIsZero() {
        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(1);
        req.setName("Spring Bouquet");
        req.setPrice(0f);
        req.setStatus(1);

        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));
        when(bouquetRepository.existsByName("Spring Bouquet")).thenReturn(true);

        assertThatThrownBy(() -> bouquetService.updateBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet price must be greater than 0");
    }

    @Test
    void updateBouquet_throwsWhenStatusIsInvalid() {
        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(1);
        req.setName("Spring Bouquet");
        req.setPrice(100_000f);
        req.setStatus(99);

        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));
        when(bouquetRepository.existsByName("Spring Bouquet")).thenReturn(true);

        assertThatThrownBy(() -> bouquetService.updateBouquet(req, Collections.emptyList()))
                .isInstanceOf(BadHttpRequestException.class)
                .hasMessageContaining("Bouquet status must be 0 or 1");
    }

    @Test
    void updateBouquet_updatesExistingMaterialQuantity() throws IOException {
        BouquetsMaterial bm = new BouquetsMaterial();
        bm.setRawMaterial(rawMaterial);
        bm.setQuantity(2);
        existingBouquet.getBouquetsMaterials().add(bm);

        UpdateBouquetRequest req = new UpdateBouquetRequest();
        req.setId(1);
        req.setName("Spring Bouquet");
        req.setPrice(100_000f);
        req.setStatus(1);
        req.setMaterials(List.of(new MaterialReq(1, 5)));

        when(bouquetRepository.findById(1)).thenReturn(Optional.of(existingBouquet));
        when(bouquetRepository.existsByName("Spring Bouquet")).thenReturn(true);
        when(rawMaterialrepository.findById(1)).thenReturn(Optional.of(rawMaterial));
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bouquetService.updateBouquet(req, Collections.emptyList());

        assertThat(existingBouquet.getBouquetsMaterials().get(0).getQuantity()).isEqualTo(5);
    }

    // ─── getMaterialCost ───────────────────────────────────────────────────────

    @Test
    void getMaterialCost_returnsLatestBatchImportPrice() {
        RawMaterialBatches batch = activeBatch(1, 200_000f, 10, 10);
        when(rawMaterialBatchRepository.findAllByRawMaterialIdAndStatus(1, EBatchStatus.ACTIVE))
                .thenReturn(List.of(batch));

        float cost = bouquetService.getMaterialCost(1);

        // Current logic: importPrice / originalQuantity → 200000 / 10 = 20000
        assertThat(cost).isEqualTo(20_000f);
    }

    @Test
    void getMaterialCost_returnsZeroWhenNoBatches() {
        when(rawMaterialBatchRepository.findAllByRawMaterialIdAndStatus(1, EBatchStatus.ACTIVE))
                .thenReturn(Collections.emptyList());

        float cost = bouquetService.getMaterialCost(1);

        assertThat(cost).isEqualTo(0f);
    }

    @Test
    void getMaterialCost_returnsZeroWhenOriginalQuantityIsZero() {
        RawMaterialBatches batch = activeBatch(1, 200_000f, 0, 0);
        when(rawMaterialBatchRepository.findAllByRawMaterialIdAndStatus(1, EBatchStatus.ACTIVE))
                .thenReturn(List.of(batch));

        float cost = bouquetService.getMaterialCost(1);

        assertThat(cost).isEqualTo(0f);
    }

    @Test
    void getMaterialCost_picksLatestBatchWhenMultipleBatches() {
        Date older = new Date(1_000_000L);
        Date newer = new Date(2_000_000L);

        RawMaterialBatches oldBatch = activeBatch(1, 200_000f, 10, 10);
        oldBatch.setImportDate(older);

        RawMaterialBatches newBatch = activeBatch(1, 300_000f, 10, 10);
        newBatch.setImportDate(newer);

        when(rawMaterialBatchRepository.findAllByRawMaterialIdAndStatus(1, EBatchStatus.ACTIVE))
                .thenReturn(List.of(oldBatch, newBatch));

        float cost = bouquetService.getMaterialCost(1);

        // picks latest (newer) batch: 300000 / 10 = 30000
        assertThat(cost).isEqualTo(30_000f);
    }

    // ─── checkInventory ────────────────────────────────────────────────────────

    @Test
    void checkInventory_setsStatusInactiveWhenMaterialInsufficient() {
        BouquetsMaterial bm = new BouquetsMaterial();
        bm.setRawMaterial(rawMaterial);
        bm.setQuantity(100);
        rawMaterial.setTotalQuantity(5); // less than required
        existingBouquet.getBouquetsMaterials().add(bm);

        when(bouquetRepository.findAll()).thenReturn(List.of(existingBouquet));
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bouquetService.checkInventory();

        assertThat(existingBouquet.getStatus()).isEqualTo(0);
    }

    @Test
    void checkInventory_setsStatusActiveWhenMaterialSufficient() {
        BouquetsMaterial bm = new BouquetsMaterial();
        bm.setRawMaterial(rawMaterial);
        bm.setQuantity(5);
        rawMaterial.setTotalQuantity(50); // enough
        existingBouquet.getBouquetsMaterials().add(bm);
        existingBouquet.setStatus(0); // currently inactive

        when(bouquetRepository.findAll()).thenReturn(List.of(existingBouquet));
        when(bouquetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        bouquetService.checkInventory();

        assertThat(existingBouquet.getStatus()).isEqualTo(1);
    }

    @Test
    void checkInventory_handlesEmptyBouquetList() {
        when(bouquetRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatCode(() -> bouquetService.checkInventory()).doesNotThrowAnyException();
        verify(bouquetRepository, never()).save(any());
    }
}
