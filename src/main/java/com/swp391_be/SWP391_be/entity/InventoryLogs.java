package com.swp391_be.SWP391_be.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391_be.SWP391_be.enums.EActionType;
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
public class InventoryLogs extends BaseEntity{
    private int quantity;
    @Enumerated(EnumType.STRING)
    private EActionType actionType;
    @ManyToOne
    @JoinColumn(name = "raw_material_batches_id", nullable = false)
    @JsonIgnore
    private RawMaterialBatches rawMaterialBatches;
}
