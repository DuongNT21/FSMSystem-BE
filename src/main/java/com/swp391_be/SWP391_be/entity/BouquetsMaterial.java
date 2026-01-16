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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BouquetsMaterial extends BaseEntity{
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "bouquet_id", nullable = false)
    @JsonIgnore
    private Bouquet bouquet;

    @OneToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    @JsonIgnore
    private RawMaterial rawMaterial;
}
