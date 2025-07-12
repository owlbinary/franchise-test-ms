package com.test.franchise.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateStockRequestDto {
    
    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;
} 