package com.swp391_be.SWP391_be.dto.response.rawMaterial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetRawMaterialResponse {
    private int id;
    private String name;
    private float importPrice;
}
