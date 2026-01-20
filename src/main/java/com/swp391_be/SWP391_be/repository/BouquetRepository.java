package com.swp391_be.SWP391_be.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.swp391_be.SWP391_be.entity.Bouquet;
public interface BouquetRepository extends JpaRepository<Bouquet, Integer>, JpaSpecificationExecutor<Bouquet> {
  boolean existsByName(String name);

}
