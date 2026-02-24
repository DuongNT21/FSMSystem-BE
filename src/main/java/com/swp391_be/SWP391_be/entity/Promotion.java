package com.swp391_be.SWP391_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Promotion extends BaseEntity{
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    private String code;
    private int discountValue;
    private Date startDate;
    private Date endDate;
    private float minOrderValue;
    private float maxDiscountValue;
}
