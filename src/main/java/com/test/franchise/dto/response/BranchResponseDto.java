package com.test.franchise.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BranchResponseDto {
    
    private Long id;
    private String name;
    private Long franchiseId;
    private String franchiseName;
    private List<ProductResponseDto> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 