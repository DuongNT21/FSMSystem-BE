package com.swp391_be.SWP391_be.dto.request.order;

import com.swp391_be.SWP391_be.enums.EOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateOrderStatusRequest {
    private EOrderStatus status;
}
