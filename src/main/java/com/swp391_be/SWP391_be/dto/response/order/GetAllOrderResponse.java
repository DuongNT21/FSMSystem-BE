package com.swp391_be.SWP391_be.dto.response.order;

import com.swp391_be.SWP391_be.enums.EOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetAllOrderResponse {
    private int id;
    private EOrderStatus orderStatus;
    private String fullName;
    private String phoneNumber;
    private float totalPrice;
    private String deliveryAddress;
}
