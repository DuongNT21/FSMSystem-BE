package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.InventoryLogs;
import com.swp391_be.SWP391_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryLogRepository extends JpaRepository<InventoryLogs, Integer> {
    List<InventoryLogs> findByRawMaterialBatchesId(int batchId);
}
