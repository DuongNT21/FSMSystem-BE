package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391_be.SWP391_be.enums.ETransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction extends BaseEntity {
    private float amount;
    @Enumerated(EnumType.STRING)
    private ETransactionStatus status;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;
}
