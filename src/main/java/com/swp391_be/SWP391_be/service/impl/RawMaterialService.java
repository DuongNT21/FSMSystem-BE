package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.CreateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.GetRawMaterialCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.UpdateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.CreateRawMaterialResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.RawMaterialRepository;
import com.swp391_be.SWP391_be.service.IRawMaterialService;
import com.swp391_be.SWP391_be.specification.RawMaterialSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        rawMaterial.setCreatedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
        CreateRawMaterialResponse materialResponse = new CreateRawMaterialResponse();
        materialResponse.setId(rawMaterial.getId());
        materialResponse.setName(rawMaterial.getName());
        return materialResponse;
    }

    @Override
    public PageResponse<GetRawMaterialResponse> getRawMaterials(GetRawMaterialCriteriaRequest criteria, int page, int size, String sort) {
        String[] sortArr = sort.split(",");
        Sort.Direction direction = sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortArr[0])
        );

        Page<RawMaterial> materialPage = rawMaterialRepository.findAll(RawMaterialSpec.byCriteria(criteria), pageable);

        return PageResponse.fromPage(materialPage, material -> {
            GetRawMaterialResponse res = new GetRawMaterialResponse();
            res.setId(material.getId());
            res.setName(material.getName());
            return res;
        });
    }

    @Override
    public GetRawMaterialResponse getRawMaterialById(int id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        GetRawMaterialResponse response = new GetRawMaterialResponse();
        response.setId(rawMaterial.getId());
        response.setName(rawMaterial.getName());
        return response;
    }

    @Override
    public GetRawMaterialResponse updateRawMaterial(int id, UpdateRawMaterialRequest request) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setName(request.getName() != null ? request.getName() : rawMaterial.getName());
        rawMaterial.setUpdatedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
        GetRawMaterialResponse response = new GetRawMaterialResponse();
        response.setId(rawMaterial.getId());
        response.setName(rawMaterial.getName());
        return response;
    }

    @Override
    public void deleteRawMaterial(int id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setDeletedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
    }
}
