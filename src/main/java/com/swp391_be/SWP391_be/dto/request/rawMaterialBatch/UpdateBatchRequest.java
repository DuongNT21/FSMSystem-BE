package com.swp391_be.SWP391_be.dto.request.rawMaterialBatch;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateBatchRequest {
    private Date expireDate;

    private float importPrice;
}
