package com.swp391_be.SWP391_be.dto.request.rawMaterial;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRawMaterialCriteriaRequest {
    private String name;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Integer minImportPrice;
    private Integer maxImportPrice;
}
