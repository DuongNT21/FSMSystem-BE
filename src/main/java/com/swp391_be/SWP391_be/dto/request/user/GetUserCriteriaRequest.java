package com.swp391_be.SWP391_be.dto.request.user;

import lombok.Data;

@Data
public class GetUserCriteriaRequest {
    private String name;
    private String username;
    private String phone;
    private String address;
}