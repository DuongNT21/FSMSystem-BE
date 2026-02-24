package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.category.CreateCategoryRequest;
import com.swp391_be.SWP391_be.dto.request.category.UpdateCategoryRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.GetRawMaterialCriteriaRequest;
import com.swp391_be.SWP391_be.dto.response.category.CreateCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.category.GetCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;

public interface ICategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest request);
    GetCategoryResponse getCategory(int id);
    PageResponse<GetCategoryResponse> getCategories(int page, int size, String name);
    GetCategoryResponse updateCategory(int id, UpdateCategoryRequest request);
    void deleteCategory(int id);
}
