package com.test.franchise.service;

import com.test.franchise.dto.request.FranchiseRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.FranchiseResponseDto;
import com.test.franchise.dto.response.TopStockProductResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FranchiseService {
    
    Mono<FranchiseResponseDto> createFranchise(FranchiseRequestDto requestDto);
    
    Mono<FranchiseResponseDto> getFranchiseById(Long id);
    
    Mono<List<FranchiseResponseDto>> getAllFranchises();
    
    Mono<FranchiseResponseDto> updateFranchiseName(Long id, UpdateNameRequestDto requestDto);
    
    Mono<Void> deleteFranchise(Long id);
    
    Mono<TopStockProductResponseDto> getTopStockProductsByFranchise(Long franchiseId);
} 