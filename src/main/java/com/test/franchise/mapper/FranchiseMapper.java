package com.test.franchise.mapper;

import com.test.franchise.domain.Franchise;
import com.test.franchise.dto.request.FranchiseRequestDto;
import com.test.franchise.dto.response.FranchiseResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BranchMapper.class})
public interface FranchiseMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branches", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Franchise toEntity(FranchiseRequestDto dto);
    
    @Mapping(target = "branches", source = "branches", qualifiedByName = "mapBranch")
    FranchiseResponseDto toDto(Franchise entity);
    
    @Mapping(target = "branches", source = "branches", qualifiedByName = "toDtoWithoutProducts")
    FranchiseResponseDto toDtoWithBranchesButWithoutProducts(Franchise entity);
    
    @Mapping(target = "branches", ignore = true)
    FranchiseResponseDto toDtoWithoutBranches(Franchise entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "branches", ignore = true)
    void updateEntity(FranchiseRequestDto dto, @MappingTarget Franchise entity);
} 