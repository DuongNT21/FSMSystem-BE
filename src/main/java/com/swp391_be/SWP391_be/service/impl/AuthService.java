package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.auth.AuthRequest;
import com.swp391_be.SWP391_be.dto.response.auth.AuthResponse;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        User user = (User) authentication.getPrincipal();
        var token = jwtService.generateToken(user);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setFullName(user.getUsername());
        return authResponse;
    }
}
