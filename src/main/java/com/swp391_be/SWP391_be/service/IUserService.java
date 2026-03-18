package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.user.GetUserCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.user.GetUserResponse;

public interface IUserService {
    RegisterResponse register(RegisterRequest registerRequest);
    PageResponse<GetUserResponse> getUsers(GetUserCriteriaRequest criteria, int page, int size, String sort);
    void updateUserStatus(int id, boolean isActive);
}
