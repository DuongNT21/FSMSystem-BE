package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;

public interface IOrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
}
