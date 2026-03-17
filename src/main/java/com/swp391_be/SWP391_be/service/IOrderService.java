package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.request.order.GetOrderCriteriaRequest;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetAllOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetOrderByIdResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface IOrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
    PageResponse<GetAllOrderResponse> getAllOrders(
            GetOrderCriteriaRequest criteria,
            int page,
            int size,
            String sort
    );
    GetOrderByIdResponse getOrderById(int orderId);

    public  String payWithVNPAYOnline(int orderId, HttpServletRequest request) throws UnsupportedEncodingException;
}
