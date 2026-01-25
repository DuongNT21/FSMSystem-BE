package com.swp391_be.SWP391_be.dto.request.rawMaterial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateRawMaterialRequest {
    private String name;
    private int quantity;
    private float importPrice;
}
