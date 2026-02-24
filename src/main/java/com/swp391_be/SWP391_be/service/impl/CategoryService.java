package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.category.CreateCategoryRequest;
import com.swp391_be.SWP391_be.dto.request.category.UpdateCategoryRequest;
import com.swp391_be.SWP391_be.dto.response.category.CreateCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.category.GetCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.entity.Category;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.CategoryRepository;
import com.swp391_be.SWP391_be.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BadHttpRequestException("Category already exists");
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        CreateCategoryResponse response = new CreateCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }

    @Override
    public GetCategoryResponse getCategory(int id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        GetCategoryResponse response = new GetCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }

    @Override
    public PageResponse<GetCategoryResponse> getCategories(int page, int size, String name) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Category> categoryPage = categoryRepository.findAllWithSearch(name, pageable);

        return PageResponse.fromPage(categoryPage, category -> {
            GetCategoryResponse res = new GetCategoryResponse();
            res.setId(category.getId());
            res.setName(category.getName());
            res.setDescription(category.getDescription());
            return res;
        });
    }

    @Override
    public GetCategoryResponse updateCategory(int id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        category.setName(request.getName() != null ? request.getName() : category.getName());
        category.setDescription(request.getDescription() != null ? request.getDescription() : category.getDescription());
        categoryRepository.save(category);
        GetCategoryResponse response = new GetCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        return response;
    }

    @Override
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}
