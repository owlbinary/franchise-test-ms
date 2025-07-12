package com.test.franchise.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponseDto {
    
    private Long id;
    private String name;
    private Integer stock;
    private Long branchId;
    private String branchName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 