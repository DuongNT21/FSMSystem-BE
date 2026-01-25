package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.CreateRawMaterialRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.CreateRawMaterialResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.service.IRawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class RawMaterialController {
    private final IRawMaterialService rawMaterialService;
    @PostMapping()
    public ResponseEntity<BaseResponse<CreateRawMaterialResponse>> createMaterial(@RequestBody CreateRawMaterialRequest request){
        CreateRawMaterialResponse response = rawMaterialService.createMaterial(request);
        BaseResponse<CreateRawMaterialResponse> baseResponse = new BaseResponse<CreateRawMaterialResponse>();
        baseResponse.setStatus(HttpStatus.CREATED.value());
        baseResponse.setMessage("Create material successfully");
        baseResponse.setData(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<List<GetRawMaterialResponse>>> getAllRawMaterial(){
        List<GetRawMaterialResponse> materialResponses = rawMaterialService.getAllRawMaterial();
        BaseResponse<List<GetRawMaterialResponse>> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Get List Raw Material Successful");
        baseResponse.setData(materialResponses);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<GetRawMaterialResponse>> getMaterialById(@PathVariable int id){
        GetRawMaterialResponse getRawMaterialResponse = rawMaterialService.getRawMaterialById(id);
        BaseResponse<GetRawMaterialResponse> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Get Raw Material Successful");
        baseResponse.setData(getRawMaterialResponse);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
