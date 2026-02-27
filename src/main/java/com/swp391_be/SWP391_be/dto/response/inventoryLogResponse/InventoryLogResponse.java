package com.swp391_be.SWP391_be.dto.response.inventoryLogResponse;

import com.swp391_be.SWP391_be.enums.EActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryLogResponse {
    private int id;
    private String rawMaterialName;
    private Integer quantity;
    private EActionType actionType;
    private LocalDateTime createdAt;
}