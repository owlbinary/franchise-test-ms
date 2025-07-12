package com.test.franchise.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FranchiseResponseDto {
    
    private Long id;
    private String name;
    private List<BranchResponseDto> branches;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 