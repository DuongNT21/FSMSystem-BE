package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;


@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    boolean existsByCode(String code);
    Optional<Promotion> findByCode(String code);
    Page<Promotion> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Promotion> findByNameContainingIgnoreCaseAndIsActive(String name, boolean status, Pageable pageable);
    boolean existsByStartDateBeforeAndEndDateAfterAndIdNot(LocalDate startDate, LocalDate endDate, Integer id);
    boolean existsByStartDateBeforeAndEndDateAfter(LocalDate startDate, LocalDate endDate);
    List<Promotion> findByIsActive(boolean active);
    Page<Promotion> findByIsActive(boolean active, Pageable pageable);

        @Query("SELECT COUNT(p) FROM Promotion p WHERE (:startDate BETWEEN p.startDate AND p.endDate OR :endDate BETWEEN p.startDate AND p.endDate) AND p.id <> :promotionId AND p.isActive = true AND p.deletedAt IS NULL")
    int countOverlappingPromotions(@Param("promotionId") Long promotionId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND :today BETWEEN p.startDate AND p.endDate AND p.deletedAt IS NULL LIMIT 1")
    Optional<Promotion> findActivePromotion(@Param("today") LocalDate today);

}