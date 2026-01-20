package com.swp391_be.SWP391_be.dto.request.bouquet;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBouquetRequest {
  private Integer id;
  private String name;
  private float price;
  private int status;
  private List<String> images;
  private List<MaterialReq> materials; 
}
