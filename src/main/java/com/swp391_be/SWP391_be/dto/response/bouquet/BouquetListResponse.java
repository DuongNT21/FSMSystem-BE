package com.swp391_be.SWP391_be.dto.response.bouquet;

import com.swp391_be.SWP391_be.entity.Bouquet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BouquetListResponse {
  private Bouquet bouquet;

  public BouquetListResponse(Bouquet bouquet) {
    this.bouquet = bouquet;
  }
}
