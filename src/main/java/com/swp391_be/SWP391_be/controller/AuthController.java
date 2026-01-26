package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.auth.AuthRequest;
import com.swp391_be.SWP391_be.dto.response.auth.AuthResponse;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.service.IAuthService;
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
public class AuthController {
    private final IAuthService iAuthService;

    @PostMapping(ApiConstant.AUTH.AUTH)
    public ResponseEntity<BaseResponse<AuthResponse>> auth(@RequestBody AuthRequest authRequest){
        AuthResponse authResponse = iAuthService.login(authRequest);
        BaseResponse<AuthResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(authResponse);
        baseResponse.setMessage("success");
        baseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
