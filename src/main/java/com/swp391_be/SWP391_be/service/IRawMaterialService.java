package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.CreateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.GetRawMaterialCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.UpdateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.CreateRawMaterialResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import jakarta.transaction.SystemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRawMaterialService {
    CreateRawMaterialResponse createMaterial(CreateRawMaterialRequest request);
    PageResponse<GetRawMaterialResponse> getRawMaterials(
            GetRawMaterialCriteriaRequest criteria,
            int page,
            int size,
            String sort
    );
    GetRawMaterialResponse getRawMaterialById(int id);
    GetRawMaterialResponse updateRawMaterial(int id, UpdateRawMaterialRequest request);
    void deleteRawMaterial(int id);
}
