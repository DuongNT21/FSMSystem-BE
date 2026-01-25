package com.swp391_be.SWP391_be.dto.response.rawMaterial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateRawMaterialResponse {
    private int id;
    private String name;
    //private LocalDateTime importDate;
    //private LocalDateTime expireDate;
    private int quantity;
    private float importPrice;

}
