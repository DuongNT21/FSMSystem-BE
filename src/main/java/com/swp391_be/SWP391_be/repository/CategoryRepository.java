package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Category;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    @Query(
            """
            SELECT COUNT(c) > 0
            FROM Category c
            WHERE c.name = :name
              AND c.deletedAt IS NULL
            """
    )
    boolean existsByNameAndDeletedAtIsNull(@Param("name") String name);

    @Query(
            """
            select c from Category c WHERE c.id = :id AND c.deletedAt is null
            """
    )
    Optional<Category> findById(@Param("id") int id);

    @Query("""
        SELECT c
        FROM Category c
        WHERE c.deletedAt IS NULL
          AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Category> findAllWithSearch(@Param("name") String name, Pageable pageable);
}
