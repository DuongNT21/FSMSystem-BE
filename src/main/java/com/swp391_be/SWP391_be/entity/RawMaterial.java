package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RawMaterial extends BaseEntity {
    private String name;
  
    @Formula("""
        (SELECT COALESCE(SUM(rmb.remain_quantity),0)
         FROM raw_material_batches rmb
         WHERE rmb.raw_material_id = id)
    """)
    private int totalQuantity;

    @OneToMany(mappedBy = "rawMaterial")
    private List<RawMaterialBatches> rawMaterialBatches;

    @OneToMany(mappedBy = "rawMaterial")
    private List<BouquetsMaterial> bouquetsMaterials;
}
