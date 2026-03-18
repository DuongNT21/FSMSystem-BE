package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.user.GetUserCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.user.GetUserResponse;
import com.swp391_be.SWP391_be.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("users")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<BaseResponse<PageResponse<GetUserResponse>>> getUsers(
            GetUserCriteriaRequest criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        PageResponse<GetUserResponse> data = iUserService.getUsers(criteria, page, size, sort);
        BaseResponse<PageResponse<GetUserResponse>> response = new BaseResponse<>();
        response.setData(data);
        response.setMessage("Get users successfully");
        response.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PutMapping( "users/{id}/status")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<BaseResponse<Void>> updateUserStatus(@PathVariable int id, @RequestParam boolean isActive) {
        iUserService.updateUserStatus(id, isActive);
        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage("Update user status successfully");
        response.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
