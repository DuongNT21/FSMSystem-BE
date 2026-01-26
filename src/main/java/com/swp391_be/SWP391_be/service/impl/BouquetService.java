package com.swp391_be.SWP391_be.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;

import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.MaterialReq;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.BouquetImage;
import com.swp391_be.SWP391_be.entity.BouquetsMaterial;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.service.IBouquetService;
import com.swp391_be.SWP391_be.specification.BouquetSpec;

@Service
public class BouquetService implements IBouquetService {

  private final BouquetRepository repository;

  public BouquetService(BouquetRepository repository) {
    this.repository = repository;
  }

  @Override
  public Bouquet createBouquet(CreateBouquetRequest bouquetRequest) {
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

    // Create bouquet images
    for (String image : bouquetRequest.getImages()) {
      BouquetImage bouquetImage = new BouquetImage();
      image = fromUrl(image);
      bouquetImage.setImage(image);
      bouquetImage.setBouquet(bouquet);
      bouquet.getImages().add(bouquetImage);
    }

    // Create bouquet materials
    // for (MaterialReq material : bouquetRequest.getMaterials()) {
    // BouquetsMaterial bouquetMaterials = new BouquetsMaterial();
    // TODO GET RAW MATERIAL
    // bouquetMaterials.setRawMaterial(material.getName());
    // bouquetMaterials.setQuantity(material.getQuantity());
    // bouquetMaterials.setBouquet(bouquet);
    // bouquet.getBouquetsMaterials().add(bouquetMaterials);
    // }
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
  public Bouquet updateBouquet(UpdateBouquetRequest bouquetRequest) {
    Optional<Bouquet> optionalBouquet = repository.findById(bouquetRequest.getId());
    if (!optionalBouquet.isPresent()) {
      throw new BadHttpRequestException("Bouquet not found");
    }
    Bouquet bouquet = optionalBouquet.get();
    String requestValidMessage = validateBouquetRequestUpdate(bouquetRequest, bouquet);

    if (requestValidMessage.equals(Strings.EMPTY)) {
      throw new BadHttpRequestException(requestValidMessage);
    }
    // TODO: Check if bouquet materials are valid (if not valid create a new request
    // for materials)

    // Update a new bouquet
    bouquet.setName(bouquetRequest.getName());
    bouquet.setPrice(bouquetRequest.getPrice());
    bouquet.setStatus(bouquetRequest.getStatus());

    // Update bouquet images
    for (String image : bouquetRequest.getImages()) {
      // Check if image is existed
      boolean isExistedImage = false;
      for (BouquetImage bouquetImage : bouquet.getImages()) {
        if (bouquetImage.getImage().equals(image)) {
          isExistedImage = true;
          break;
        }
      }
      if (!isExistedImage) {
        BouquetImage bouquetImage = new BouquetImage();
        bouquetImage.setImage(image);
        bouquetImage.setBouquet(bouquet);
        bouquet.getImages().add(bouquetImage);
      }
    }

    // Update bouquet materials
    // for (MaterialReq material : bouquetRequest.getMaterials()) {
    // BouquetsMaterial bouquetMaterials = new BouquetsMaterial();
    // TODO GET RAW MATERIAL
    // bouquetMaterials.setRawMaterial(material.getName());
    // bouquetMaterials.setQuantity(material.getQuantity());
    // bouquetMaterials.setBouquet(bouquet);
    // bouquet.getBouquetsMaterials().add(bouquetMaterials);
    // }
    return repository.save(bouquet);
  }

  @Override
  public PageResponse<BouquetListResponse> getBouquets(Pageable pageable, GetBouquetCriteriaRequest getBouquetRequest) {
    Page<Bouquet> page = repository.findAll(BouquetSpec.byCriteria(getBouquetRequest), pageable);
    return PageResponse.fromPage(page, BouquetListResponse::new);
  }

  private String validateBouquetRequestUpdate(UpdateBouquetRequest bouquetRequest, Bouquet bouquet) {
    // Get bouquet by id
    if (bouquetRequest.getId() == null || bouquetRequest.getId() <= 0) {
      return "Bouquet id is required";
    }
    // Check if bouquet name is valid
    if (bouquetRequest.getName() == null || bouquetRequest.getName().isEmpty()) {
      return "Bouquet name is required";
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

  
    public String fromUrl(String imageUrl) {
        try (InputStream is = new URL(imageUrl).openStream()) {
            byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new BadHttpRequestException("Không thể tải ảnh từ URL");
        }
    }
}
