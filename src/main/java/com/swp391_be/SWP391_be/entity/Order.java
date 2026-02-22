package com.swp391_be.SWP391_be.entity;

import com.swp391_be.SWP391_be.enums.EOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private EOrderStatus orderStatus;
    private float totalPrice;
    private String deliveryAddress;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order")
    private List<OrderPromotion> orderPromotions;

    @OneToMany(mappedBy = "order")
    private List<Transaction> transactions;
}
