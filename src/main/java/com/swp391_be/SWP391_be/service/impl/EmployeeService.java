package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.employee.CreateEmployeeRequest;
import com.swp391_be.SWP391_be.dto.response.employee.CreateEmployeeResponse;
import com.swp391_be.SWP391_be.entity.Employee;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.entity.UserProfile;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.repository.EmployeeRepository;
import com.swp391_be.SWP391_be.repository.RoleRepository;
import com.swp391_be.SWP391_be.repository.UserProfileRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    @Override
    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest createEmployeeRequest) {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        List<User> users = userRepository.findAll();

        if(users.stream().anyMatch(u -> u.getUsername().equals(createEmployeeRequest.getUsername()))) {
            throw new BadHttpRequestException("Username is already taken");
        }
        if(users.stream().anyMatch(u -> u.getEmail().equals(createEmployeeRequest.getEmail()))) {
            throw new BadHttpRequestException("Email is already taken");
        }
        if(userProfiles.stream().anyMatch(u -> u.getPhone().equals(createEmployeeRequest.getPhone()))) {
            throw new BadHttpRequestException("Phone number is already taken");
        }

        User user = new User();
        UserProfile userProfile = new UserProfile();
        Employee employee = new Employee();
        LocalDateTime now = LocalDateTime.now();
        user.setUsername(createEmployeeRequest.getUsername());
        user.setEmail(createEmployeeRequest.getEmail());
        user.setCreatedAt(now);
        user.setPassword(passwordEncoder.encode(createEmployeeRequest.getPassword()));

        userRepository.save(user);
        userProfile.setUser(user);
        userProfile.setPhone(createEmployeeRequest.getPhone());
        userProfile.setAvatar(createEmployeeRequest.getAvatar());
        userProfile.setName(createEmployeeRequest.getFullName());
        userProfile.setAddress(createEmployeeRequest.getAddress());
        userProfile.setCreatedAt(now);
        userProfileRepository.save(userProfile);

        employee.setUser(user);
        employee.setRole(roleRepository.findByRoleName("Staff"));
        employeeRepository.save(employee);

        CreateEmployeeResponse createEmployeeResponse = new CreateEmployeeResponse();
        createEmployeeResponse.setAvatar(userProfile.getAvatar());
        createEmployeeResponse.setFullName(userProfile.getName());
        createEmployeeResponse.setUsername(user.getUsername());
        createEmployeeResponse.setId(user.getId());
        return createEmployeeResponse;
    }
}
