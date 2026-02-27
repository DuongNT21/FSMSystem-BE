package com.swp391_be.SWP391_be.dto.request.rawMaterialBatch;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateRawMaterialBatchRequest {
    private Date importDate;
    private Date expireDate;
    private float importPrice;
    private int rawMaterialId;
    private int originalQuantity;
}
