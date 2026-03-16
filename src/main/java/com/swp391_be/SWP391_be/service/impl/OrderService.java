package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.request.order.GetOrderCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.orderItems.CreateOrderItemsRequest;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetAllOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetOrderByIdResponse;
import com.swp391_be.SWP391_be.dto.response.orderItem.GetOrderItemResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.entity.*;
import com.swp391_be.SWP391_be.enums.EOrderStatus;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.repository.OrderRepository;
import com.swp391_be.SWP391_be.repository.PromotionRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IOrderService;
import com.swp391_be.SWP391_be.specification.OrderSpecification;
import com.swp391_be.SWP391_be.util.AuthenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BouquetRepository bouquetRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        int userId = AuthenUtil.getCurrentUserId();
        User user = new User();
        Order order = new Order();
        float totalPrice = 0;
        Optional<Promotion> promotion = promotionRepository.findActivePromotion(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        if (userId != 0) {
            user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        }
        order.setFullName(request.getFullName() == null ? user.getUserProfile().getName() : request.getFullName());
        order.setPhoneNumber(
                request.getPhoneNumber() == null ? user.getUserProfile().getPhone() : request.getPhoneNumber());
        order.setDeliveryAddress(request.getDeliveryAddress() == null ? user.getUserProfile().getAddress()
                : request.getDeliveryAddress());
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(EOrderStatus.Pending);
        order.setCreatedAt(LocalDateTime.now());
        if (user != null) {
            order.setUser(user);
        }

        for (CreateOrderItemsRequest orderItemReq : request.getOrderItems()) {
            Bouquet bouquet = bouquetRepository.findById(orderItemReq.getBouquetId())
                    .orElseThrow(() -> new NotFoundException("Bouquet not found"));
            for (BouquetsMaterial bm : bouquet.getBouquetsMaterials()) {
                RawMaterial rawMaterial = bm.getRawMaterial();

                int requiredQuantity = bm.getQuantity() * orderItemReq.getQuantity();

                if (rawMaterial.getTotalQuantity() < requiredQuantity) {
                    throw new BadHttpRequestException("Not enough raw material");
                }
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setBouquet(bouquet);
            orderItem.setQuantity(orderItemReq.getQuantity());
            orderItem.setPrice(bouquet.getPrice() * orderItemReq.getQuantity());
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setOrder(order);
            totalPrice += bouquet.getPrice() * orderItemReq.getQuantity();
            orderItems.add(orderItem);
        }
        Promotion promo = promotion.isPresent() ? promotion.get() : null;
        float subtotal = totalPrice; 
        float vatAmount = subtotal * 0.10f; 
        float finalPrice = subtotal;
        if (promo != null) {
            float discountPercent = promo.getDiscountValue() / 100f; 
            // Apply discount ONLY to the subtotal
            finalPrice = subtotal - (subtotal * discountPercent);
        }
        order.setTotalPrice(finalPrice + vatAmount);
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

    @Override
    public PageResponse<GetAllOrderResponse> getAllOrders(
            GetOrderCriteriaRequest criteria,
            int page,
            int size,
            String sort) {

        int userId = AuthenUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        String[] sortArr = sort.split(",");
        Sort.Direction direction = sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortArr[0]));

        Specification<Order> spec = OrderSpecification.byCriteria(criteria);

        if (user.getRole().getRoleName().equals("User")) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        } else {
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get("orderStatus"), EOrderStatus.Pending));
        }

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        return PageResponse.fromPage(orderPage, order -> {
            GetAllOrderResponse res = new GetAllOrderResponse();
            res.setId(order.getId());
            res.setOrderStatus(order.getOrderStatus());
            res.setFullName(order.getFullName());
            res.setPhoneNumber(order.getPhoneNumber());
            res.setTotalPrice(order.getTotalPrice());
            res.setDeliveryAddress(order.getDeliveryAddress());
            return res;
        });
    }

    @Override
    public GetOrderByIdResponse getOrderById(int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        GetOrderByIdResponse response = new GetOrderByIdResponse();
        response.setId(order.getId());
        response.setOrderStatus(order.getOrderStatus());
        response.setFullName(order.getFullName());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setTotalPrice(order.getTotalPrice());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setOrderItems(order.getOrderItems()
                .stream()
                .map(oi -> {
                    GetOrderItemResponse item = new GetOrderItemResponse();
                    item.setId(oi.getId());
                    item.setBouquetName(oi.getBouquet().getName());
                    item.setBouquetDescription(oi.getBouquet().getDescription());
                    item.setQuantity(oi.getQuantity());
                    item.setPrice(oi.getPrice());
                    return item;
                }).toList());
        return response;
    }

}
