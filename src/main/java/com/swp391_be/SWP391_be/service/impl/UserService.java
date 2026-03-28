package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.user.GetUserCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.user.GetUserResponse;
import com.swp391_be.SWP391_be.entity.Role;
import com.swp391_be.SWP391_be.entity.Employee;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.entity.UserProfile;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.EmployeeRepository;
import com.swp391_be.SWP391_be.repository.RoleRepository;
import com.swp391_be.SWP391_be.repository.UserProfileRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IUserService;
import com.swp391_be.SWP391_be.util.AuthenUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
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

    @Override
    public PageResponse<GetUserResponse> getUsers(GetUserCriteriaRequest criteria, int page, int size, String sort) {
        int currentUserId = AuthenUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("Requester not found"));
        String roleName = currentUser.getRole().getRoleName();

        Sort.Direction direction = Sort.Direction.ASC;
        String sortBy = "id";

        if (sort != null) {
            String[] sortArr = sort.split(",");
            if (sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sortBy = sortArr[0];
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (roleName.equalsIgnoreCase("Admin")) {
                predicates.add(cb.notEqual(root.get("id"), currentUserId));
            } else if (roleName.equalsIgnoreCase("Staff")) {
                predicates.add(cb.equal(root.get("role").get("roleName"), "User"));
            }

            Join<User, UserProfile> profileJoin = root.join("userProfile", JoinType.LEFT);

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + criteria.getUsername().toLowerCase() + "%"));
            }
            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(profileJoin.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }
            if (criteria.getPhone() != null && !criteria.getPhone().isEmpty()) {
                predicates.add(cb.like(profileJoin.get("phone"), "%" + criteria.getPhone() + "%"));
            }
            if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
                predicates.add(cb.like(cb.lower(profileJoin.get("address")), "%" + criteria.getAddress().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> users = userRepository.findAll(spec, pageable);

        return PageResponse.fromPage(users, user -> {
            GetUserResponse res = new GetUserResponse();
            res.setId(user.getId());
            res.setUsername(user.getUsername());
            res.setEmail(user.getEmail());
            res.setRole(user.getRole().getRoleName());
            res.setActive(user.getIsActive() != null ? user.getIsActive() : true);
            if (user.getUserProfile() != null) {
                res.setFullName(user.getUserProfile().getName());
                res.setPhone(user.getUserProfile().getPhone());
                res.setAddress(user.getUserProfile().getAddress());
                res.setAvatar(user.getUserProfile().getAvatar());
            }
            return res;
        });
    }

    @Override
    public void updateUserStatus(int id, boolean isActive) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole().getRoleName().equalsIgnoreCase("Admin")|| user.getRole().getRoleName().equalsIgnoreCase("Staff")) {
            throw new BadHttpRequestException("Cannot update status of admin user");
        }
        user.setIsActive(isActive);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public RegisterResponse createStaff(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadHttpRequestException("Username is already taken");
        }
        
        LocalDateTime now = LocalDateTime.now();
        Role staffRole = roleRepository.findByRoleName("Staff");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(staffRole);
        user.setCreatedAt(now);
        user.setIsActive(true);
        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setName(request.getFullName());
        profile.setPhone(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setAvatar(request.getAvatar());
        profile.setCreatedAt(now);
        userProfileRepository.save(profile);

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setRole(staffRole);
        employeeRepository.save(employee);

        RegisterResponse response = new RegisterResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(profile.getName());
        response.setAvatar(profile.getAvatar());
        return response;
    }

    @Override
    @Transactional
    public RegisterResponse updateStaff(int id, RegisterRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Staff not found"));
        if (!user.getRole().getRoleName().equalsIgnoreCase("Staff")) {
            throw new BadHttpRequestException("User is not a staff member");
        }

        UserProfile profile = user.getUserProfile();
        profile.setName(request.getFullName());
        profile.setPhone(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setAvatar(request.getAvatar());
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(profile);

        RegisterResponse response = new RegisterResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(profile.getName());
        response.setAvatar(profile.getAvatar());
        return response;
    }

    @Override
    @Transactional
    public void deleteStaff(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Staff not found"));
        if (!user.getRole().getRoleName().equalsIgnoreCase("Staff")) {
            throw new BadHttpRequestException("Only staff members can be deleted via this API");
        }
        // Delete associated employee record first if JPA doesn't handle cascading
        employeeRepository.findByUserId(id).ifPresent(employeeRepository::delete);
        userRepository.delete(user);
    }
}
