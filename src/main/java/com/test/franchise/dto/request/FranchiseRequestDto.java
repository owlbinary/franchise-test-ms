package com.test.franchise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FranchiseRequestDto {
    
    @NotBlank(message = "Franchise name is required")
    @Size(min = 2, max = 100, message = "Franchise name must be between 2 and 100 characters")
    private String name;
} 