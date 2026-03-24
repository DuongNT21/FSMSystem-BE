package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String fullName;
    private String phoneNumber;
    private float totalPrice;
    private String deliveryAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order")
    private List<OrderPromotion> orderPromotions;

    @OneToMany(mappedBy = "order")
    private List<Transaction> transactions;
}
