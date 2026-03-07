package com.swp391_be.SWP391_be.dto.response.bouquet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BouquetCostResponse {
    private int bouquetId;
    private float totalCost;
    private List<MaterialCostItem> breakdown;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialCostItem {
        private int rawMaterialId;
        private String rawMaterialName;
        private int quantity;
        private float unitPrice;
        private float subtotal;
    }
}
