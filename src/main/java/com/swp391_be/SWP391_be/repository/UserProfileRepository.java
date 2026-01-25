package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
}
