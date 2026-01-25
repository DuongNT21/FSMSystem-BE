package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.employee.CreateEmployeeRequest;
import com.swp391_be.SWP391_be.dto.response.employee.CreateEmployeeResponse;

public interface IEmployeeService {
    CreateEmployeeResponse createEmployee(CreateEmployeeRequest createEmployeeRequest);
}
