package com.swp391_be.SWP391_be.service.schedule;

import com.swp391_be.SWP391_be.entity.Promotion;
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
  ScheduleTask(PromotionRepository promotionRepository) {
    this.promotionRepository = promotionRepository;
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
    
}
