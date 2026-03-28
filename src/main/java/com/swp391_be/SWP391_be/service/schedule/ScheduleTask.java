package com.swp391_be.SWP391_be.service.schedule;

import com.swp391_be.SWP391_be.entity.Order;
import com.swp391_be.SWP391_be.entity.Promotion;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.enums.ETransactionStatus;
import com.swp391_be.SWP391_be.repository.OrderRepository;
import com.swp391_be.SWP391_be.repository.PromotionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleTask {
  private final PromotionRepository promotionRepository;
  private final OrderRepository orderRepository;
  private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

  ScheduleTask(PromotionRepository promotionRepository, OrderRepository orderRepository) {
    this.promotionRepository = promotionRepository;
    this.orderRepository = orderRepository;
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

  @Scheduled(fixedRate = 60000)
  @Transactional(rollbackFor = RuntimeException.class)
  public void cancelStaleOrders() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
    List<Order> staleOrders = orderRepository.findPendingOrders(
        EOrderStatus.Pending, cutoff);
    for (Order order : staleOrders) {
      order.setOrderStatus(EOrderStatus.Cancelled);
      try {
        orderRepository.save(order);
        logger.info("Auto-cancelled order id={} (payment confirmed but pending > 30 min)", order.getId());
      } catch (Exception e) {
        logger.error("Failed to cancel stale order id={}: {}", order.getId(), e.getMessage());
      }
    }
  }
}
