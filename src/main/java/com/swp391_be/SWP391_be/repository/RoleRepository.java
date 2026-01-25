package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleName(String roleName);
}
