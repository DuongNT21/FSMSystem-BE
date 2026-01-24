package com.swp391_be.SWP391_be.dto.response.bouquet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBouquetResponse {
    private int id;
    private String name;
    private int status;
    private float price;
}
