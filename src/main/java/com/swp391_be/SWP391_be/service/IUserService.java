package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;

public interface IUserService {
    RegisterResponse register(RegisterRequest registerRequest);
}
