package com.test.franchise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequestDto {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;
    
    @NotNull(message = "Branch ID is required")
    @PositiveOrZero(message = "Branch ID must be positive")
    private Long branchId;
} 