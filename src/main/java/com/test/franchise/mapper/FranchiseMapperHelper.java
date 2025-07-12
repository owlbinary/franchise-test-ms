package com.test.franchise.mapper;

import com.test.franchise.domain.Franchise;
import com.test.franchise.dto.response.FranchiseResponseDto;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FranchiseMapperHelper {
    
    @Autowired
    private BranchMapper branchMapper;
    
    public FranchiseResponseDto toDtoSafe(Franchise entity) {
        if (entity == null) {
            return null;
        }
        
        var builder = FranchiseResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());  
        
        if (Hibernate.isInitialized(entity.getBranches()) && entity.getBranches() != null) {
            builder.branches(entity.getBranches().stream()
                    .map(branchMapper::toDto)
                    .toList());
        } else {
            builder.branches(new ArrayList<>());
        }
        
        return builder.build();
    }
    
    public FranchiseResponseDto toDtoWithoutBranches(Franchise entity) {
        if (entity == null) {
            return null;
        }
        
        return FranchiseResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .branches(new ArrayList<>())
                .build();
    }
} 