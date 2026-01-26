package com.swp391_be.SWP391_be.dto.response.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeResponse {
    private int id;
    private String username;
    private String fullName;
    private String avatar;
}
