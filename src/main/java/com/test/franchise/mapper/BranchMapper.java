package com.test.franchise.mapper;

import com.test.franchise.domain.Branch;
import com.test.franchise.dto.request.BranchRequestDto;
import com.test.franchise.dto.response.BranchResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface BranchMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "franchise", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Branch toEntity(BranchRequestDto dto);
    
    @Mapping(target = "franchiseId", source = "franchise.id")
    @Mapping(target = "franchiseName", source = "franchise.name")
    @Mapping(target = "products", source = "products", qualifiedByName = "mapProduct")
    BranchResponseDto toDto(Branch entity);
    
    @Named("mapBranch")
    @Mapping(target = "franchiseId", source = "franchise.id")
    @Mapping(target = "franchiseName", source = "franchise.name")
    @Mapping(target = "products", source = "products", qualifiedByName = "mapProduct")
    BranchResponseDto mapBranch(Branch entity);
    
    @Named("toDtoWithoutProducts")
    @Mapping(target = "franchiseId", source = "franchise.id")
    @Mapping(target = "franchiseName", source = "franchise.name")
    @Mapping(target = "products", ignore = true)
    BranchResponseDto toDtoWithoutProducts(Branch entity);
    
    @Mapping(target = "franchiseId", ignore = true)
    @Mapping(target = "franchiseName", ignore = true)
    @Mapping(target = "products", ignore = true)
    BranchResponseDto toDtoWithoutLazyFields(Branch entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "franchise", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BranchRequestDto dto, @MappingTarget Branch entity);
} 