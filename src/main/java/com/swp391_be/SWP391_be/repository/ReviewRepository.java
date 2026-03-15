package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("""
        SELECT r
        FROM Review r
        WHERE r.bouquet.id = :bouquetId
        ORDER BY
            CASE WHEN :sort = 'asc' THEN r.rating END ASC,
            CASE WHEN :sort = 'desc' THEN r.rating END DESC
    """)
    Page<Review> getAllReviews(@Param("bouquetId") int bouquetId,
                               @Param("sort") String sort,
                               Pageable pageable);
}
