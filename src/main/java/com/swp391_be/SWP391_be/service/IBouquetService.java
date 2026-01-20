package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.CreateBouquetResponse;

public interface IBouquetService {
    CreateBouquetResponse createBouquet(CreateBouquetRequest request);
}
