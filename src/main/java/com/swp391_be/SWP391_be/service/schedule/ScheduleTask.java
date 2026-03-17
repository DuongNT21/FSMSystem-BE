package com.swp391_be.SWP391_be.service.schedule;

import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.BouquetsMaterial;
import com.swp391_be.SWP391_be.entity.Promotion;
import com.swp391_be.SWP391_be.entity.RawMaterial;
import com.swp391_be.SWP391_be.exception.AppExceptionHandler;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.repository.PromotionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduleTask {
  private final PromotionRepository promotionRepository;
  private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);
  private final BouquetRepository bouquetRepository;

  ScheduleTask(PromotionRepository promotionRepository, BouquetRepository bouquetRepository) {
    this.promotionRepository = promotionRepository;
    this.bouquetRepository = bouquetRepository;
  }

  @Scheduled(fixedRate = 5000)
  @Transactional(rollbackFor = RuntimeException.class)
  public void updatePromotionStatus() {
    // Logic to update promotion status daily
    System.out.println("Updating promotion status...");

    List<Promotion> activePromotions = promotionRepository.findByIsActive(true);
    for (Promotion promotion : activePromotions) {
      LocalDate now = LocalDate.now();
      if (now.isAfter(promotion.getEndDate())) {
        promotion.setActive(false);
        try {
          promotionRepository.save(promotion);
        } catch (Exception e) {
          System.out.println("Failed to update promotion status with message:" + e.getMessage());
          e.printStackTrace();
        }

      }
    }
  }

  @Scheduled(fixedRate = 6000)
  @Transactional(rollbackFor = RuntimeException.class)
  public void checkInventory() {
    List<Bouquet> bouquets = bouquetRepository.findAll();
    for (Bouquet bouquet : bouquets) {
      boolean isOutOfStock = false;
      for (BouquetsMaterial bm : bouquet.getBouquetsMaterials()) {
        RawMaterial rawMaterial = bm.getRawMaterial();

        int requiredQuantity = bm.getQuantity();

        if (rawMaterial.getTotalQuantity() < requiredQuantity) {
          isOutOfStock = true;
          break;
        }
      }
      if (isOutOfStock) {
        // update bouquet status to out of stock
        bouquet.setStatus(0);
        try {
          bouquetRepository.save(bouquet);
        } catch (Exception e) {
          System.out.println("Failed to update bouquet status with message:" + e.getMessage());
          e.printStackTrace();
        }
      } else {
        bouquet.setStatus(1);
        try {
          bouquetRepository.save(bouquet);
        } catch (Exception e) {
          System.out.println("Failed to update bouquet status with message:" + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }
}
