package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    boolean existsByCode(String code);
    Optional<Promotion> findByCode(String code);
    Page<Promotion> findByNameContainingIgnoreCase(String name, Pageable pageable);
}