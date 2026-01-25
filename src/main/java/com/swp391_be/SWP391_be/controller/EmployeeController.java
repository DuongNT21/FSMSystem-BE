package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.employee.CreateEmployeeRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.employee.CreateEmployeeResponse;
import com.swp391_be.SWP391_be.entity.Role;
import com.swp391_be.SWP391_be.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.API)
public class EmployeeController {
    private final IEmployeeService employeeService;

    @PostMapping(ApiConstant.EMPLOYEE.EMPLOYEE)
    public ResponseEntity<BaseResponse<CreateEmployeeResponse>> createEmployee(@RequestBody CreateEmployeeRequest createEmployeeRequest){
        CreateEmployeeResponse createEmployeeResponse = employeeService.createEmployee(createEmployeeRequest);
        BaseResponse<CreateEmployeeResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(createEmployeeResponse);
        baseResponse.setMessage("Create employee successfully");
        baseResponse.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

}
