package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.RawMaterialBatches;
import com.swp391_be.SWP391_be.enums.EBatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RawMaterialBatchRepository extends JpaRepository<RawMaterialBatches, Integer> {

    @Query("SELECT COALESCE(SUM(b.remainQuantity), 0) FROM RawMaterialBatches b WHERE b.rawMaterial.id = :rawMaterialId")
    int sumRemainQuantityByRawMaterialId(@Param("rawMaterialId") int rawMaterialId);

    @Query("SELECT b FROM RawMaterialBatches b WHERE b.rawMaterial.id = :rawMaterialId AND b.status = :status ORDER BY b.importDate DESC")
    Optional<RawMaterialBatches> findLatestBatchByRawMaterialIdAndStatus(
            @Param("rawMaterialId") int rawMaterialId,
            @Param("status") EBatchStatus status);
}
