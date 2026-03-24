package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Order;
import com.swp391_be.SWP391_be.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrder(Order order);
}
