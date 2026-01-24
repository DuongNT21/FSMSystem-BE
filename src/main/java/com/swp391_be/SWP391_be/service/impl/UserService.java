package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.entity.Role;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.entity.UserProfile;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.repository.RoleRepository;
import com.swp391_be.SWP391_be.repository.UserProfileRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAll();
        Role role = roleRepository.findByRoleName("User");
        if (users.stream().anyMatch(user -> user.getUsername().equals(registerRequest.getUsername()))) {
            throw new BadHttpRequestException("Username is already taken");
        }
        if (users.stream().anyMatch(user -> user.getEmail().equals(registerRequest.getEmail()))) {
            throw new BadHttpRequestException("Email is already taken");
        }


        User user = new User();
        UserProfile userProfile = new UserProfile();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(now);
        user.setRole(role);
        userRepository.save(user);
        userProfile.setAddress(registerRequest.getAddress());
        userProfile.setName(registerRequest.getFullName());
        userProfile.setPhone(registerRequest.getPhoneNumber());
        userProfile.setAvatar(registerRequest.getAvatar());
        userProfile.setUser(user);
        userProfile.setCreatedAt(now);
        userProfileRepository.save(userProfile);

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(user.getId());
        registerResponse.setUsername(user.getUsername());
        registerResponse.setFullName(userProfile.getName());
        registerResponse.setAvatar(userProfile.getAvatar());
        return  registerResponse;
    }
}
