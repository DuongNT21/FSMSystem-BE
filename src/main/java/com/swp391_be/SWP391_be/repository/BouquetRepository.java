package com.swp391_be.SWP391_be.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swp391_be.SWP391_be.entity.Bouquet;
public interface BouquetRepository extends JpaRepository<Bouquet, Integer>, JpaSpecificationExecutor<Bouquet> {
  boolean existsByName(String name);

  @Query("SELECT b FROM Bouquet b JOIN b.reviews r WHERE r.createdAt BETWEEN :start AND :end GROUP BY b ORDER BY COUNT(r) DESC")
  List<Bouquet> findMostRatedBouquetsInTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

  @Query("SELECT b FROM Bouquet b LEFT JOIN b.reviews r WHERE b.status = 1 GROUP BY b ORDER BY COUNT(r) DESC, COUNT(DISTINCT r.user) DESC")
  List<Bouquet> findTopRatedBouquets(Pageable pageable);

}
