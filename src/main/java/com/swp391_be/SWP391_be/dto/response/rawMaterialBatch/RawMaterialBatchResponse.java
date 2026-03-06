package com.swp391_be.SWP391_be.dto.response.rawMaterialBatch;


import com.swp391_be.SWP391_be.dto.response.inventoryLogResponse.InventoryLogResponse;
import com.swp391_be.SWP391_be.enums.EBatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialBatchResponse {

    private int id;
    private Date importDate;
    private Date expireDate;
    private Float importPrice;
    private int  originalQuantity;
    private int remainQuantity;
    private int rawMaterialId;
    private String rawMaterialName;
    private EBatchStatus status;

    private List<InventoryLogResponse> logs;
}