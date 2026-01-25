package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterial extends BaseEntity {
    private String name;
    private LocalDateTime importDate;
    private LocalDateTime expireDate;
    private int quantity;
    private float importPrice;

    @OneToOne(mappedBy = "rawMaterial")
    private BouquetsMaterial bouquetsMaterial;
}
