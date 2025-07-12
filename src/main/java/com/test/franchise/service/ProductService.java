package com.test.franchise.service;

import com.test.franchise.dto.request.ProductRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.request.UpdateStockRequestDto;
import com.test.franchise.dto.response.ProductResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {
    
    Mono<ProductResponseDto> createProduct(ProductRequestDto requestDto);
    
    Mono<ProductResponseDto> getProductById(Long id);
    
    Mono<List<ProductResponseDto>> getProductsByBranchId(Long branchId);
    
    Mono<ProductResponseDto> updateProductName(Long id, UpdateNameRequestDto requestDto);
    
    Mono<ProductResponseDto> updateProductStock(Long id, UpdateStockRequestDto requestDto);
    
    Mono<Void> deleteProduct(Long id);
} 