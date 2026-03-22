package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.entity.Order;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.repository.OrderRepository;
import com.swp391_be.SWP391_be.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    private final OrderRepository orderRepository;

    @Override
    public void paymentSuccess(int id) {
        Order order = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Order not found"));
        order.setOrderStatus(EOrderStatus.Accepted);

        orderRepository.save(order);
    }
}
