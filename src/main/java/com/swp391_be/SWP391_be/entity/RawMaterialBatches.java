package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterialBatches extends BaseEntity{
    private Date importDate;
    private Date expireDate;
    private float importPrice;
    private int originalQuantity;
    private int remainQuantity;
    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    @JsonIgnore
    private RawMaterial rawMaterial;

    @OneToMany(mappedBy = "rawMaterialBatches")
    private List<InventoryLogs> inventoryLogs;
}

