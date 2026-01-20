package com.swp391_be.SWP391_be.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.responseException.BadRequestException;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.BouquetImage;
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
    
    // Check if bouquet name already exists
    boolean isExisted = repository.existsByName(bouquetRequest.getName());
    if (isExisted) {
      throw new BadRequestException("Bouquet name already exists");
    }
    // Check if bouquet price is valid
    if (bouquetRequest.getPrice() <= 0) {
      throw new BadRequestException("Bouquet price must be greater than 0");
    }
    // Check if bouquet status is valid
    if (bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_INACTIVE && bouquetRequest.getStatus() != ApiConstant.BOUQUET_STATUS_ACTIVE) {
      throw new BadRequestException("Bouquet status must be 0 or 1");
    }
    // TODO: Check if bouquet materials are valid (if not valid create a new request for materials)

    // Create a new bouquet
    Bouquet bouquet = new Bouquet();
    bouquet.setName(bouquetRequest.getName());
    bouquet.setPrice(bouquetRequest.getPrice());
    bouquet.setStatus(bouquetRequest.getStatus());

    for (String image : bouquetRequest.getImages()) {
      BouquetImage bouquetImage = new BouquetImage();
      bouquetImage.setImage(image);
      bouquetImage.setBouquet(bouquet);
      bouquet.getImages().add(bouquetImage);
    }
    return repository.save(bouquet);
  }

  @Override
  public void deleteBouquet(int id) {
    // TODO Auto-generated method stub

  }

  @Override
  public Bouquet getById(int id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Bouquet updateBouquet(UpdateBouquetRequest bouquet) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PageResponse<BouquetListResponse> getBouquets(Pageable pageable, GetBouquetCriteriaRequest getBouquetRequest) {
    Page<Bouquet> page = repository.findAll(BouquetSpec.byCriteria(getBouquetRequest), pageable);
    return PageResponse.fromPage(page, BouquetListResponse::new);
  }
}
