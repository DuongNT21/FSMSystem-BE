package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.employee.CreateEmployeeResponse;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;
import com.swp391_be.SWP391_be.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.API)
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping(ApiConstant.ORDER.ORDER)
    public ResponseEntity<BaseResponse<CreateOrderResponse>> createOrder(@RequestBody CreateOrderRequest createOrderRequest){
        CreateOrderResponse createOrderResponse = orderService.createOrder(createOrderRequest);
        BaseResponse<CreateOrderResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(createOrderResponse);
        baseResponse.setMessage("Create order successfully");
        baseResponse.setStatus(HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }
}
