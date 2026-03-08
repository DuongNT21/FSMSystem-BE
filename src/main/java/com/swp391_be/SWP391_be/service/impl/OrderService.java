package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.request.orderItems.CreateOrderItemsRequest;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.Order;
import com.swp391_be.SWP391_be.entity.OrderItem;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.repository.OrderRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IOrderService;
import com.swp391_be.SWP391_be.util.AuthenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BouquetRepository bouquetRepository;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        int userId = AuthenUtil.getCurrentUserId();
        User user = new User();
        Order order = new Order();
        float totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        if (userId != 0) {
            user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        }
        order.setFullName(request.getFullName() == null ? user.getUserProfile().getName() : request.getFullName());
        order.setPhoneNumber(request.getPhoneNumber() == null ? user.getUserProfile().getPhone() : request.getPhoneNumber());
        order.setDeliveryAddress(request.getDeliveryAddress() == null ? user.getUserProfile().getAddress() : request.getDeliveryAddress());
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(EOrderStatus.Pending);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (CreateOrderItemsRequest orderItemReq : request.getOrderItems()) {
            Bouquet bouquet = bouquetRepository.findById(orderItemReq.getBouquetId()).orElseThrow(() -> new NotFoundException("Bouquet not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setBouquet(bouquet);
            orderItem.setQuantity(orderItemReq.getQuantity());
            orderItem.setPrice(bouquet.getPrice());
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setOrder(order);
            totalPrice += orderItem.getPrice() * orderItemReq.getQuantity();
            orderItems.add(orderItem);
        }
        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        CreateOrderResponse response = new CreateOrderResponse();
        response.setId(order.getId());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setFullName(order.getFullName());
        response.setTotalPrice(order.getTotalPrice());
        return response;
    }
}
