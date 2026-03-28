package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Employee;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByUserId(int userId);
}
