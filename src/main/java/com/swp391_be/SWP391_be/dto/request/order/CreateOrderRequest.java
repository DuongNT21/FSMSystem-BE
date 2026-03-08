package com.swp391_be.SWP391_be.dto.request.order;

import com.swp391_be.SWP391_be.dto.request.orderItems.CreateOrderItemsRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderRequest {
    private String fullName;
    private String phoneNumber;
    private String deliveryAddress;
    private List<CreateOrderItemsRequest> orderItems;
}
