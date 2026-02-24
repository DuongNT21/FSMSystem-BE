package com.swp391_be.SWP391_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE promotions SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Promotion extends BaseEntity{
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(unique = true)
    private String code;
    private int discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private float minOrderValue;
    private float maxDiscountValue;
}
