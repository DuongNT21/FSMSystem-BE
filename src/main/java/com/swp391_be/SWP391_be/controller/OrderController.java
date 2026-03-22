package com.swp391_be.SWP391_be.controller;

import com.swp391_be.SWP391_be.constant.ApiConstant;
import com.swp391_be.SWP391_be.dto.request.employee.CreateEmployeeRequest;
import com.swp391_be.SWP391_be.dto.request.order.CreateOrderRequest;
import com.swp391_be.SWP391_be.dto.request.order.GetOrderCriteriaRequest;
import com.swp391_be.SWP391_be.dto.request.order.UpdateOrderStatusRequest;
import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import com.swp391_be.SWP391_be.dto.response.employee.CreateEmployeeResponse;
import com.swp391_be.SWP391_be.dto.response.order.CreateOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetAllOrderResponse;
import com.swp391_be.SWP391_be.dto.response.order.GetOrderByIdResponse;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.rawMaterial.GetRawMaterialResponse;
import com.swp391_be.SWP391_be.service.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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

    @GetMapping(ApiConstant.ORDER.ORDER)
    public ResponseEntity<BaseResponse<PageResponse<GetAllOrderResponse>>> getOrders(
            GetOrderCriteriaRequest criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PageResponse<GetAllOrderResponse> data = orderService.getAllOrders(criteria, page, size, sort);

        BaseResponse<PageResponse<GetAllOrderResponse>> response = new BaseResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get order list successfully");
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @GetMapping(ApiConstant.ORDER.ID)
    public ResponseEntity<BaseResponse<GetOrderByIdResponse>> getOrderById(@PathVariable int id){
        GetOrderByIdResponse getOrderByIdResponse = orderService.getOrderById(id);
        BaseResponse<GetOrderByIdResponse> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Get Raw Material Successful");
        baseResponse.setData(getOrderByIdResponse);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @PatchMapping(ApiConstant.ORDER.STATUS)
    public ResponseEntity<BaseResponse<Void>> updateOrderStatus(@PathVariable int id, @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, request);
        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Order status updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiConstant.ORDER.PAY)
    public ResponseEntity<BaseResponse<String>> payment(@PathVariable int id, HttpServletRequest request) throws UnsupportedEncodingException {
        String url = orderService.payWithVNPAYOnline(id, request);
        BaseResponse<String> response = new BaseResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Payment successfully");
        response.setData(url);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
