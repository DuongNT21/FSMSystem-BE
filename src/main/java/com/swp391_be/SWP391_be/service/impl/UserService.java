package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.user.GetUserCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.user.RegisterRequest;
import com.swp391_be.SWP391_be.dto.response.RegisterResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.user.GetUserResponse;
import com.swp391_be.SWP391_be.entity.Role;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.entity.UserProfile;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
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
            predicates.add(cb.notEqual(root.get("id"), currentUserId));

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
        if (user.getRole().getRoleName().equalsIgnoreCase("Admin")) {
            throw new BadHttpRequestException("Cannot update status of admin user");
        }
        user.setIsActive(isActive);
        userRepository.save(user);
    }
}
