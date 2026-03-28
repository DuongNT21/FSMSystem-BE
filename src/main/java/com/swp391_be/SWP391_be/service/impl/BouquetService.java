package com.swp391_be.SWP391_be.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.swp391_be.SWP391_be.exception.BadHttpRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.MaterialReq;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetCostResponse;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.entity.RawMaterialBatches;
import com.swp391_be.SWP391_be.enums.EBatchStatus;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.BouquetImage;
import com.swp391_be.SWP391_be.entity.BouquetsMaterial;
import com.swp391_be.SWP391_be.entity.Category;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.repository.CategoryRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialBatchRepository;
import com.swp391_be.SWP391_be.repository.RawMaterialRepository;
import com.swp391_be.SWP391_be.service.IBouquetService;
import com.swp391_be.SWP391_be.specification.BouquetSpec;
import com.swp391_be.SWP391_be.service.IImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BouquetService implements IBouquetService {

  private final BouquetRepository repository;
  private final RawMaterialRepository rawMaterialrepository;
  private final RawMaterialBatchRepository rawMaterialBatchRepository;
  private final CategoryRepository categoryRepository;
  private final IImageService imageService;

  @Override
  public Bouquet createBouquet(CreateBouquetRequest bouquetRequest, List<MultipartFile> images) {
    // Check if bouquet name is valid
    if (bouquetRequest.getName() == null || bouquetRequest.getName().isEmpty()) {
      throw new BadHttpRequestException("Bouquet name is required");
    }
    // Check if bouquet name already exists
    boolean isExisted = repository.existsByName(bouquetRequest.getName());
    if (isExisted) {
      throw new BadHttpRequestException("Bouquet name already exists");
    }
    // Check if bouquet price is valid
    if (bouquetRequest.getPrice() <= 0) {
      throw new BadHttpRequestException("Bouquet price must be greater than 0");
    }
    // Check if bouquet status is valid
    if (bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_INACTIVE
        && bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_ACTIVE) {
      throw new BadHttpRequestException("Bouquet status must be 0 or 1");
    }
    // TODO: Check if bouquet materials are valid (if not valid create a new request
    // for materials)

    // Create a new bouquet
    Bouquet bouquet = new Bouquet();
    bouquet.setName(bouquetRequest.getName());
    bouquet.setPrice(bouquetRequest.getPrice());
    bouquet.setStatus(bouquetRequest.getStatus());
    bouquet.setDescription(bouquetRequest.getDescription());
    if (bouquetRequest.getCategoryId() != null) {
      Category category = categoryRepository.findById(bouquetRequest.getCategoryId())
          .orElseThrow(() -> new BadHttpRequestException("Category not found"));
      bouquet.setCategory(category);
    }

    // Create bouquet images
    for (MultipartFile file : images) {

      try {
        Map<String, String> uploadResult = imageService.uploadImage(file);

        BouquetImage bouquetImage = new BouquetImage();
        bouquetImage.setImage(uploadResult.get("url"));
        bouquetImage.setPublicId(uploadResult.get("publicId"));
        bouquetImage.setBouquet(bouquet);

        bouquet.getImages().add(bouquetImage);

      } catch (IOException e) {
        throw new BadHttpRequestException("Image upload failed");
      }
    }

    // Create bouquet materials
    processMaterials(bouquet, bouquetRequest.getMaterials());

    return repository.save(bouquet);
  }

  @Override
  public void deleteBouquet(int id) {
    Optional<Bouquet> optionalBouquet = repository.findById(id);
    if (!optionalBouquet.isPresent()) {
      throw new BadHttpRequestException("Bouquet not found");
    }
    repository.delete(optionalBouquet.get());
  }

  @Override
  public Bouquet getById(int id) {
    Optional<Bouquet> optionalBouquet = repository.findById(id);
    if (!optionalBouquet.isPresent()) {
      throw new BadHttpRequestException("Bouquet not found");
    }
    Bouquet bouquet = optionalBouquet.get();
    if (bouquet.getImages() != null) {
      bouquet.getImages().forEach(BouquetImage::getBouquet);
    }
    if (bouquet.getBouquetsMaterials() != null) {
      bouquet.getBouquetsMaterials().forEach(BouquetsMaterial::getBouquet);
    }
    return bouquet;
  }

  @Override
  public Bouquet updateBouquet(UpdateBouquetRequest bouquetRequest, List<MultipartFile> images) {

    Optional<Bouquet> optionalBouquet = repository.findById(bouquetRequest.getId());
    if (!optionalBouquet.isPresent()) {
      throw new BadHttpRequestException("Bouquet not found");
    }
    Bouquet bouquet = optionalBouquet.get();
    String requestValidMessage = validateBouquetRequestUpdate(bouquetRequest, bouquet);

    if (!requestValidMessage.equals(Strings.EMPTY)) {
      throw new BadHttpRequestException(requestValidMessage);
    }

    // Update bouquet fields
    bouquet.setName(bouquetRequest.getName());
    bouquet.setDescription(bouquetRequest.getDescription());
    bouquet.setPrice(bouquetRequest.getPrice());
    bouquet.setStatus(bouquetRequest.getStatus());
    if (bouquetRequest.getCategoryId() != null) {
      Category category = categoryRepository.findById(bouquetRequest.getCategoryId())
          .orElseThrow(() -> new BadHttpRequestException("Category not found"));
      bouquet.setCategory(category);
    } else {
      bouquet.setCategory(null);
    }

    // Delete bouquet images
    if (bouquetRequest.getDeleteImages() != null) {
      for (int imageId : bouquetRequest.getDeleteImages()) {
        Optional<BouquetImage> optionalBouquetImage = bouquet.getImages().stream()
            .filter(image -> image.getId() == imageId).findFirst();
        if (optionalBouquetImage.isPresent()) {
          BouquetImage toDelete = optionalBouquetImage.get();
          try {
            if (toDelete.getPublicId() != null) {
              imageService.deleteImage(toDelete.getPublicId());
            }
          } catch (IOException e) {
            throw new BadHttpRequestException("Image delete failed");
          }
          bouquet.getImages().remove(toDelete);
        }
      }
    }

    // Upload and add new images
    if (images != null) {
      for (MultipartFile file : images) {
        try {
          Map<String, String> uploadResult = imageService.uploadImage(file);
          BouquetImage bouquetImage = new BouquetImage();
          bouquetImage.setImage(uploadResult.get("url"));
          bouquetImage.setPublicId(uploadResult.get("publicId"));
          bouquetImage.setBouquet(bouquet);
          bouquet.getImages().add(bouquetImage);
        } catch (IOException e) {
          throw new BadHttpRequestException("Image upload failed");
        }
      }
    }

    // Update bouquet materials
    processMaterials(bouquet, bouquetRequest.getMaterials());

    return repository.save(bouquet);
  }

  @Override
  public PageResponse<BouquetListResponse> getBouquets(Pageable pageable, GetBouquetCriteriaRequest getBouquetRequest) {
    Page<Bouquet> page = repository.findAll(BouquetSpec.byCriteria(getBouquetRequest), pageable);
    return PageResponse.fromPage(page, BouquetListResponse::new);
  }

  private void processMaterials(Bouquet bouquet, List<MaterialReq> materials) {
    if (materials == null || materials.isEmpty()) {
      return;
    }
    int totalRequestMaterialQuantities = 0;
    int totalRawMaterialQuantities = 0;
    String rawMaterialName = null;
    for (MaterialReq materialRequest : materials) {
      // Check if raw material exists
      Optional<BouquetsMaterial> existing = bouquet.getBouquetsMaterials().stream()
          .filter(bm -> bm.getRawMaterial().getId() == materialRequest.getId())
          .findFirst();
      RawMaterial rawMaterial = rawMaterialrepository.findById(materialRequest.getId())
          .orElseThrow(() -> new BadHttpRequestException("Raw material not found"));
      totalRequestMaterialQuantities = materialRequest.getQuantity();
      totalRawMaterialQuantities = rawMaterial.getTotalQuantity();
      if (rawMaterialName == null) {
        rawMaterialName = rawMaterial.getName();
      }
      if (totalRequestMaterialQuantities <= 0) {
        throw new BadHttpRequestException("Material quantity must be greater than 0");
      }

      if (totalRawMaterialQuantities < totalRequestMaterialQuantities) {
        throw new BadHttpRequestException("Not enough raw material in stock: " + rawMaterialName
            + " (available: " + totalRawMaterialQuantities + ", required: " + totalRequestMaterialQuantities + ")");
      }

      if (existing.isPresent()) {
        existing.get().setQuantity(materialRequest.getQuantity());
      } else {
        BouquetsMaterial bouquetMaterial = new BouquetsMaterial();
        bouquetMaterial.setRawMaterial(rawMaterial);
        bouquetMaterial.setQuantity(materialRequest.getQuantity());
        bouquetMaterial.setBouquet(bouquet);
        bouquet.getBouquetsMaterials().add(bouquetMaterial);
      }
    }

  }

  private String validateBouquetRequestUpdate(UpdateBouquetRequest bouquetRequest, Bouquet bouquet) {
    // Get bouquet by id
    if (bouquetRequest.getId() == null || bouquetRequest.getId() <= 0) {
      return "Bouquet id is required";
    }
    // Check if bouquet name is valid
    String name = bouquetRequest.getName();
    if (bouquetRequest.getName() == null || bouquetRequest.getName().isEmpty()) {
      return "Bouquet name is required";
    }
    boolean existingBouquet = repository.existsByName(name);
    if (existingBouquet && !bouquet.getName().equals(bouquetRequest.getName())) {
      return "Bouquet name already exists";
    }
    // Check if bouquet name already exists
    boolean isExisted = repository.existsByName(bouquetRequest.getName());
    if (isExisted && !bouquet.getName().equals(bouquetRequest.getName())) {
      return "Bouquet name already exists";
    }
    // Check if bouquet price is valid
    if (bouquetRequest.getPrice() <= 0) {
      return "Bouquet price must be greater than 0";
    }
    // Check if bouquet status is valid
    if (bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_INACTIVE
        && bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_ACTIVE) {
      return "Bouquet status must be 0 or 1";
    }
    return "";
  }

  @Override
  public float getMaterialCost(int materialId) {
    return rawMaterialBatchRepository
        .findAllByRawMaterialIdAndStatus(materialId, EBatchStatus.ACTIVE)
        .stream()
        .max(Comparator.comparing(RawMaterialBatches::getImportDate))
        .map(batch -> batch.getOriginalQuantity() > 0
            ? batch.getImportPrice() / batch.getOriginalQuantity()
            : 0f)
        .orElse(0f);
  }

  @Override
  public List<Bouquet> getMostRatedBouquetsToday() {
    LocalDateTime start = LocalDate.now().atStartOfDay();
    LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
    return repository.findMostRatedBouquetsInTimeRange(start, end, PageRequest.of(0, 1));
  }

  @Override
  public List<Bouquet> getTop4RatedBouquets() {
    return repository.findTopRatedBouquets(PageRequest.of(0, 4));
  }

  @Override
  public void checkInventory() {
    List<Bouquet> bouquets = repository.findAll();
    for (Bouquet bouquet : bouquets) {
      boolean isOutOfStock = false;
      if (!bouquet.getBouquetsMaterials().isEmpty()) {
        for (BouquetsMaterial bm : bouquet.getBouquetsMaterials()) {
          RawMaterial rawMaterial = bm.getRawMaterial();
          int requiredQuantity = bm.getQuantity();
          if (rawMaterial.getTotalQuantity() < requiredQuantity) {
            isOutOfStock = true;
            break;
          }
        }
      } else {
        isOutOfStock = true;
      }

      bouquet.setStatus(isOutOfStock ? 0 : 1);
      try {
        repository.save(bouquet);
      } catch (Exception e) {
        System.out.println("Failed to update bouquet status with message:" + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
