package com.test.franchise.service;

import com.test.franchise.dto.request.BranchRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.BranchResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BranchService {
    
    Mono<BranchResponseDto> createBranch(BranchRequestDto requestDto);
    
    Mono<BranchResponseDto> getBranchById(Long id);
    
    Mono<List<BranchResponseDto>> getBranchesByFranchiseId(Long franchiseId);
    
    Mono<BranchResponseDto> updateBranchName(Long id, UpdateNameRequestDto requestDto);
    
    Mono<Void> deleteBranch(Long id);
} 