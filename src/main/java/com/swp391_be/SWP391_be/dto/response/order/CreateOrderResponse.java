package com.swp391_be.SWP391_be.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateOrderResponse {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String deliveryAddress;
    private float totalPrice;
}
