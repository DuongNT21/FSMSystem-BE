package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.bouquet.CreateBouquetRequest;
import com.swp391_be.SWP391_be.dto.response.bouquet.CreateBouquetResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.service.IBouquetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BouquetService implements IBouquetService {
    private final BouquetRepository bouquetRepository;

    @Override
    public CreateBouquetResponse createBouquet(CreateBouquetRequest request) {
        Bouquet bouquet = new Bouquet();
        bouquet.setName(request.getName());
        bouquet.setStatus(request.getStatus());
        bouquet.setPrice(request.getPrice());
        bouquet.setCreatedAt(LocalDateTime.now());
        bouquet.setUpdatedAt(LocalDateTime.now());
        bouquetRepository.save(bouquet);
        CreateBouquetResponse bouquetResponse = new CreateBouquetResponse();
        bouquetResponse.setId(bouquet.getId());
        bouquetResponse.setName(bouquet.getName());
        bouquetResponse.setStatus(bouquet.getStatus());
        bouquetResponse.setPrice(bouquetResponse.getPrice());
        return bouquetResponse;

    }
}
