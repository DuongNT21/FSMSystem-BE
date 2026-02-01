package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.CreateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.UpdateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.CreateRawMaterialResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.RawMaterialRepository;
import com.swp391_be.SWP391_be.service.IRawMaterialService;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RawMaterialService implements IRawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;

    @Override
    @Transactional
    public CreateRawMaterialResponse createMaterial(CreateRawMaterialRequest request) {
        if (rawMaterialRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BadHttpRequestException("Raw material already exists");
        }
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setName(request.getName());
        rawMaterial.setQuantity(request.getQuantity());
        rawMaterial.setImportPrice(request.getImportPrice());
        rawMaterial.setCreatedAt(LocalDateTime.now());
        rawMaterial.setImportDate(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
        CreateRawMaterialResponse materialResponse = new CreateRawMaterialResponse();
        materialResponse.setId(rawMaterial.getId());
        materialResponse.setName(rawMaterial.getName());
        materialResponse.setQuantity(rawMaterial.getQuantity());
        materialResponse.setImportPrice(rawMaterial.getImportPrice());
        return materialResponse;
    }

    @Override
    public List<GetRawMaterialResponse> getAllRawMaterial() {
        List<RawMaterial> materials = rawMaterialRepository.findAll(); //lay ra tat ca raw material
        //materials la 1 list
        //material la tung phan tu trong list
        //truoc dau "->" la input dau vao
        //sau dau "->" la logic cua minh
        List<GetRawMaterialResponse> responses = materials.stream().map(material -> {
            GetRawMaterialResponse response = new GetRawMaterialResponse();
            response.setId(material.getId());
            response.setName(material.getName());
            response.setQuantity(material.getQuantity());
            response.setImportPrice(material.getImportPrice());
            return response;
        }).collect(Collectors.toList());
        return responses;
    }

    @Override
    public GetRawMaterialResponse getRawMaterialById(int id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        GetRawMaterialResponse response = new GetRawMaterialResponse();
        response.setId(rawMaterial.getId());
        response.setName(rawMaterial.getName());
        response.setQuantity(rawMaterial.getQuantity());
        response.setImportPrice(rawMaterial.getImportPrice());
        return response;
    }

    @Override
    public GetRawMaterialResponse updateRawMaterial(int id, UpdateRawMaterialRequest request) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setName(request.getName() != null ? request.getName() : rawMaterial.getName());
        rawMaterial.setQuantity(request.getQuantity() != 0 ? request.getQuantity() : rawMaterial.getQuantity());
        rawMaterial.setImportPrice(request.getImportPrice() != 0 ? request.getImportPrice() : rawMaterial.getImportPrice());
        rawMaterial.setUpdatedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
        GetRawMaterialResponse response = new GetRawMaterialResponse();
        response.setId(rawMaterial.getId());
        response.setName(rawMaterial.getName());
        response.setQuantity(rawMaterial.getQuantity());
        response.setImportPrice(rawMaterial.getImportPrice());
        return response;
    }

    @Override
    public void deleteRawMaterial(int id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setDeletedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
    }
}
