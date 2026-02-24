package com.swp391_be.SWP391_be.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetCategoryResponse {
    private int id;
    private String name;
    private String description;
}
