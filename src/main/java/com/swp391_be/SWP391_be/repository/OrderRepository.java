package com.swp391_be.SWP391_be.repository;

import com.swp391_be.SWP391_be.entity.Order;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.enums.ETransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    @Query("SELECT DISTINCT o FROM Order o "  +
            "WHERE o.orderStatus = :orderStatus  AND o.createdAt < :cutoff")
    List<Order> findPendingOrders(
            @Param("orderStatus") EOrderStatus orderStatus,
            @Param("cutoff") LocalDateTime cutoff);
}
