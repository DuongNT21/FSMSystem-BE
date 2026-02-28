package com.swp391_be.SWP391_be.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCategoryRequest {
    @NotBlank(message = "Name is required")
    @NotNull(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;
    @NotBlank(message = "Description is required")
    @NotNull(message = "Description is required")
    private String description;
}
