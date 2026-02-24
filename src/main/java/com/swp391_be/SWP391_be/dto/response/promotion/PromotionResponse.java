package com.swp391_be.SWP391_be.dto.response.promotion;

import com.swp391_be.SWP391_be.entity.Promotion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PromotionResponse {
    private int id;
    private String name;
    private String code;
    private String description;
    private int discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private float minOrderValue;
    private float maxDiscountValue;
    private LocalDateTime createdAt;

    public PromotionResponse(Promotion promotion) {
        this.id = promotion.getId();
        this.name = promotion.getName();
        this.code = promotion.getCode();
        this.description = promotion.getDescription();
        this.discountValue = promotion.getDiscountValue();
        this.startDate = promotion.getStartDate();
        this.endDate = promotion.getEndDate();
        this.minOrderValue = promotion.getMinOrderValue();
        this.maxDiscountValue = promotion.getMaxDiscountValue();
        this.createdAt = promotion.getCreatedAt();
    }
}
