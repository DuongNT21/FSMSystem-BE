package com.swp391_be.SWP391_be.dto.request.bouquet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialReq {
  private String name;
  private int quantity;

  public MaterialReq(String name, int quantity) {
    this.name = name;
    this.quantity = quantity;
  }
  
}
