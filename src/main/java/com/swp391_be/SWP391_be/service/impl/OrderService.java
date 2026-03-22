package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.configuration.PaymentConfig;
import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.request.order.GetOrderCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.order.UpdateOrderStatusRequest;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final BouquetRepository bouquetRepository;
    private final PromotionRepository promotionRepository;
    private PaymentConfig paymentConfig;

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
        System.out.println("debug");
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

    @Override
    public String payWithVNPAYOnline(int orderId, HttpServletRequest request) throws UnsupportedEncodingException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        long totalPrice = (long)order.getTotalPrice(); // VD: 100000

        long deposit = totalPrice;
        long vnp_Amount = deposit;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 10);

        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", PaymentConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(String.valueOf(vnp_Amount) + "00"));
        vnp_Params.put("vnp_BankCode", PaymentConfig.vnp_BankCode);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode", PaymentConfig.vnp_CurrCode);
        vnp_Params.put("vnp_IpAddr", PaymentConfig.getIpAddress(request));
        vnp_Params.put("vnp_Locale", PaymentConfig.vnp_Locale);
        vnp_Params.put("vnp_OrderInfo", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_TxnRef", "HD" + RandomStringUtils.randomNumeric(6) + "-" + vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldList = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldList);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator itr = fieldList.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }

    @Override
    public void updateOrderStatus(int orderId, UpdateOrderStatusRequest request) {
        if (request.getStatus() == null) {
            throw new BadHttpRequestException("Order status is required");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setOrderStatus(request.getStatus());
        orderRepository.save(order);
    }

}
