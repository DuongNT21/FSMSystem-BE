package com.swp391_be.SWP391_be.dto.request.rawMaterial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateRawMaterialRequest {
    @NotBlank(message = "Name is required")
    @NotNull(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
}
