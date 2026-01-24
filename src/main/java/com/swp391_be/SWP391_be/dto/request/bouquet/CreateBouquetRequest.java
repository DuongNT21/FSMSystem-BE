package com.swp391_be.SWP391_be.dto.request.bouquet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateBouquetRequest {
    private String name;
    private int status;
    private float price;
}
