package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.auth.AuthRequest;
import com.swp391_be.SWP391_be.dto.response.auth.AuthResponse;

public interface IAuthService {
    AuthResponse login(AuthRequest authRequest);
}
