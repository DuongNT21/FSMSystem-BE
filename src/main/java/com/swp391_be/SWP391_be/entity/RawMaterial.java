package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterial extends BaseEntity {
    private String name;
    private int quantity;
    private float importPrice;

    @OneToMany(mappedBy = "rawMaterial")
    private List<RawMaterialBatches> rawMaterialBatches;

    @OneToMany(mappedBy = "rawMaterial")
    private List<BouquetsMaterial> bouquetsMaterials;
}
