package com.test.franchise.mapper;

import com.test.franchise.domain.Product;
import com.test.franchise.dto.request.ProductRequestDto;
import com.test.franchise.dto.response.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequestDto dto);
    
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    ProductResponseDto toDto(Product entity);
    
    @Named("mapProduct")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    ProductResponseDto mapProduct(Product entity);
    
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    ProductResponseDto toDtoWithoutBranch(Product entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ProductRequestDto dto, @MappingTarget Product entity);
} 