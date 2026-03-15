package com.swp391_be.SWP391_be.dto.request.promotion;

import java.time.LocalDate;

import lombok.Data;

  @Data
public class UpdatePromotionRequest {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private int discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private float minOrderValue;
    private float maxDiscountValue;
    private boolean status;
}
