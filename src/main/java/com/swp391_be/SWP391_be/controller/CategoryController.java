package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.dto.request.category.CreateCategoryRequest;
import com.swp391_be.SWP391_be.dto.request.category.UpdateCategoryRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.GetRawMaterialCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.rawMaterial.UpdateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.category.CreateCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.category.GetCategoryResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final ICategoryService categoryService;
    @PostMapping()
    public ResponseEntity<BaseResponse<CreateCategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request){
        CreateCategoryResponse response = categoryService.createCategory(request);
        BaseResponse<CreateCategoryResponse> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.CREATED.value());
        baseResponse.setMessage("Create category successfully");
        baseResponse.setData(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GetCategoryResponse>> getCategoryById(@PathVariable int id){
        GetCategoryResponse response = categoryService.getCategory(id);
        BaseResponse<GetCategoryResponse> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Get Category Successful");
        baseResponse.setData(response);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<GetCategoryResponse>>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        PageResponse<GetCategoryResponse> data = categoryService.getCategories(page, size, name);
        BaseResponse<PageResponse<GetCategoryResponse>> response = new BaseResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get Category List Successfully");
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<GetCategoryResponse>> updateCategory(@PathVariable int id, @RequestBody UpdateCategoryRequest request){
        GetCategoryResponse getCategoryResponse = categoryService.updateCategory(id, request);
        BaseResponse<GetCategoryResponse> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Update Category Successful");
        baseResponse.setData(getCategoryResponse);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteCategory(@PathVariable int id){
        categoryService.deleteCategory(id);
        BaseResponse<?> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Delete Category Successful");
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
