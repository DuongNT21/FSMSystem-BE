package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.API)
public class UserController {
    private final IUserService iUserService;

    @PostMapping(ApiConstant.USER.USER)
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = iUserService.register(registerRequest);
        BaseResponse<RegisterResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(registerResponse);
        baseResponse.setMessage("Register successfully");
        baseResponse.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }
}
