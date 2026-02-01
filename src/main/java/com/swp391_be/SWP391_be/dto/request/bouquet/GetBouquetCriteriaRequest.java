package com.swp391_be.SWP391_be.dto.request.bouquet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetBouquetCriteriaRequest {
  private String name;
  private Integer status;
  private Float minPrice;
  private Float maxPrice;
}
