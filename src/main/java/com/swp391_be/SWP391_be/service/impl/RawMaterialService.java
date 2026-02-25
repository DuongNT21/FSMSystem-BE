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
import com.swp391_be.SWP391_be.util.AuthenUtil;

import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final AuthenUtil authenUtil;

    @Override
    @Transactional
    public CreateRawMaterialResponse createMaterial(CreateRawMaterialRequest request) {
        int userId = authenUtil.getCurrentUserId();
        if (userId == -1) {
            throw new BadHttpRequestException("Unauthorized");
        }
        if (rawMaterialRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BadHttpRequestException("Raw material already exists");
        }
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setName(request.getName());
        rawMaterial.setQuantity(request.getQuantity());
        rawMaterial.setImportPrice(request.getImportPrice());
        rawMaterial.setCreatedAt(LocalDateTime.now());
        rawMaterial.setImportDate(LocalDateTime.now());
        rawMaterial.setUserId(userId);
        rawMaterialRepository.save(rawMaterial);
        CreateRawMaterialResponse materialResponse = new CreateRawMaterialResponse();
        materialResponse.setId(rawMaterial.getId());
        materialResponse.setName(rawMaterial.getName());

        // materialResponse.setQuantity(rawMaterial.getQuantity());
        // materialResponse.setImportPrice(rawMaterial.getImportPrice());
        return materialResponse;
    }

    @Override
    public PageResponse<GetRawMaterialResponse> getRawMaterials(GetRawMaterialCriteriaRequest criteria, int page,
            int size, String sort) {
        String[] sortArr = sort.split(",");
        Sort.Direction direction = sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortArr[0]));

        Page<RawMaterial> materialPage = rawMaterialRepository.findAll(RawMaterialSpec.byCriteria(criteria), pageable);

        return PageResponse.fromPage(materialPage, material -> {
            GetRawMaterialResponse res = new GetRawMaterialResponse();
            res.setId(material.getId());
            res.setName(material.getName());
            res.setQuantity(material.getQuantity());
            res.setImportPrice(material.getImportPrice());
            return res;
        });
    }

    @Override
    public GetRawMaterialResponse getRawMaterialById(int id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));
        GetRawMaterialResponse response = new GetRawMaterialResponse();
        response.setId(rawMaterial.getId());
        response.setName(rawMaterial.getName());
        response.setQuantity(rawMaterial.getQuantity());
        response.setImportPrice(rawMaterial.getImportPrice());
        return response;
    }

    @Override
    public GetRawMaterialResponse updateRawMaterial(int id, UpdateRawMaterialRequest request) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setName(request.getName() != null ? request.getName() : rawMaterial.getName());
        rawMaterial.setQuantity(request.getQuantity() != 0 ? request.getQuantity() : rawMaterial.getQuantity());
        rawMaterial.setImportPrice(
                request.getImportPrice() != 0 ? request.getImportPrice() : rawMaterial.getImportPrice());
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
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));
        rawMaterial.setDeletedAt(LocalDateTime.now());
        rawMaterialRepository.save(rawMaterial);
    }
}
