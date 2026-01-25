package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.CreateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.CreateRawMaterialResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRawMaterialService {
    CreateRawMaterialResponse createMaterial(CreateRawMaterialRequest request);
    List<GetRawMaterialResponse> getAllRawMaterial();
    GetRawMaterialResponse getRawMaterialById(int id);
}
