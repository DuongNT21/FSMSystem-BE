package com.swp391_be.SWP391_be.dto.response.user;

import lombok.Data;

@Data
public class GetUserResponse {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;
    private boolean isActive;
    private String avatar;
}