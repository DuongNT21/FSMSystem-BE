package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
