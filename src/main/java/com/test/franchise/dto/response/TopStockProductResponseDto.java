package com.test.franchise.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TopStockProductResponseDto {
    
    private Long franchiseId;
    private String franchiseName;
    private List<BranchTopProduct> branchTopProducts;
    
    @Data
    @Builder
    public static class BranchTopProduct {
        private Long branchId;
        private String branchName;
        private Long productId;
        private String productName;
        private Integer stock;
    }
} 