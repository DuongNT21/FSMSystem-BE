package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Integer> {
    @Query(
        """
        select r from RawMaterial r WHERE r.deletedAt is null
        """
    )
    List<RawMaterial> findAll();

    @Query(
        """
        select r from RawMaterial r WHERE r.id = :id AND r.deletedAt is null
        """
    )
    Optional<RawMaterial> findById(@Param("id") int id);

    @Query(
        """
        SELECT COUNT(r) > 0
        FROM RawMaterial r
        WHERE r.name = :name
          AND r.deletedAt IS NULL
        """
    )
    boolean existsByNameAndDeletedAtIsNull(@Param("name") String name);
}
