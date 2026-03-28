package com.swp391_be.SWP391_be.dto.request.bouquet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaterialReq {
  private int id;
  private Integer batchId;
  private int quantity;

  public MaterialReq(int id, Integer batchId, int quantity) {
    this.id = id;
    this.batchId = batchId;
    this.quantity = quantity;
  }
}
