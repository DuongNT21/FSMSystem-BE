package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.RawMaterialBatches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RawMaterialBatchRepository extends JpaRepository<RawMaterialBatches, Integer> {

    @Query("SELECT COALESCE(SUM(b.remainQuantity), 0) FROM RawMaterialBatches b WHERE b.rawMaterial.id = :rawMaterialId")
    int sumRemainQuantityByRawMaterialId(@Param("rawMaterialId") int rawMaterialId);
}
