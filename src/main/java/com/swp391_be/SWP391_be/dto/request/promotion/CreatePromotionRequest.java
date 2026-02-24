package com.swp391_be.SWP391_be.dto.request.promotion;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePromotionRequest {
    private String name;
    private String code;
    private String description;
    private int discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private float minOrderValue;
    private float maxDiscountValue;
}