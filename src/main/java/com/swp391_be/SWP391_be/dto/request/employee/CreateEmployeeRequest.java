package com.swp391_be.SWP391_be.dto.request.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeRequest {
    private String email;
    private String password;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
}
