package com.swp391_be.SWP391_be.service;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.bouquet.UpdateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.BouquetListResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;

public interface IBouquetService {
  Bouquet createBouquet(CreateBouquetRequest bouquetRequest, List<MultipartFile> images);
  Bouquet getById(int id);
  Bouquet updateBouquet(UpdateBouquetRequest bouquet);
  void deleteBouquet(int id);
  PageResponse<BouquetListResponse> getBouquets(Pageable pageable, GetBouquetCriteriaRequest getBouquetRequest);
}
