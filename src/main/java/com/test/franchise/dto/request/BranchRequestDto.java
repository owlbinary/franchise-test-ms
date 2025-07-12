package com.test.franchise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BranchRequestDto {
    
    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Franchise ID is required")
    @Positive(message = "Franchise ID must be positive")
    private Long franchiseId;
} 