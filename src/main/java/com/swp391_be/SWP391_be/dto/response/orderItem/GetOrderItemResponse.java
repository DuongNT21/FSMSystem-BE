package com.swp391_be.SWP391_be.dto.response.orderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOrderItemResponse {
    private int id;
    private String bouquetName;
    private String bouquetDescription;
    private int quantity;
    private float price;
}
