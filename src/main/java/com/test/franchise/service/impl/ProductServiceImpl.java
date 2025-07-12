package com.test.franchise.service.impl;

import com.test.franchise.domain.Branch;
import com.test.franchise.domain.Product;
import com.test.franchise.dto.request.ProductRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.request.UpdateStockRequestDto;
import com.test.franchise.dto.response.ProductResponseDto;
import com.test.franchise.exception.DuplicateEntityException;
import com.test.franchise.exception.EntityNotFoundException;
import com.test.franchise.mapper.ProductMapper;
import com.test.franchise.repository.BranchRepository;
import com.test.franchise.repository.ProductRepository;
import com.test.franchise.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final ProductMapper productMapper;

    @Override
    public Mono<ProductResponseDto> createProduct(ProductRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Creating product with name: {} for branch ID: {}", requestDto.getName(), requestDto.getBranchId());
            
            Branch branch = branchRepository.findById(requestDto.getBranchId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + requestDto.getBranchId()));
            
            if (productRepository.existsByNameAndBranchId(requestDto.getName(), requestDto.getBranchId())) {
                throw new DuplicateEntityException("Product with name '" + requestDto.getName() + "' already exists for this branch");
            }
            
            Product product = productMapper.toEntity(requestDto);
            product.setBranch(branch);
            Product savedProduct = productRepository.save(product);
            
            Product reloadedProduct = productRepository.findByIdWithBranch(savedProduct.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found after save"));
            
            ProductResponseDto result = productMapper.toDto(reloadedProduct);
            log.info("Product created successfully with ID: {} - Result: {}", savedProduct.getId(), result);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductResponseDto> getProductById(Long id) {
        return Mono.fromCallable(() -> {
            log.info("Fetching product with ID: {}", id);
            
            Optional<Product> productOpt = productRepository.findByIdWithBranch(id);
            
            if (productOpt.isEmpty()) {
                log.info("Product not found with ID: {}", id);
                return null;
            }
            
            Product product = productOpt.get();
            ProductResponseDto result = productMapper.toDto(product);
            log.info("Product fetched successfully with ID: {} - Result: {}", id, result);
            return result;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .filter(result -> result != null);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<List<ProductResponseDto>> getProductsByBranchId(Long branchId) {
        return Mono.fromCallable(() -> {
            log.info("Fetching products for branch ID: {}", branchId);
            
            List<Product> products = productRepository.findByBranchIdWithBranch(branchId);
            
            if (products.isEmpty()) {
                log.info("No products found for branch ID: {}", branchId);
                return List.<ProductResponseDto>of();
            }
            
            List<ProductResponseDto> result = products.stream()
                    .map(product -> {
                        ProductResponseDto dto = productMapper.toDto(product);
                        log.debug("Mapped product: {} -> {}", product.getName(), dto);
                        return dto;
                    })
                    .toList();
                    
            log.info("Successfully fetched {} products for branch ID: {}", result.size(), branchId);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ProductResponseDto> updateProductName(Long id, UpdateNameRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Updating product name for ID: {} to: {}", id, requestDto.getName());
            
            Product product = productRepository.findByIdWithBranch(id)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
            
            if (!product.getName().equals(requestDto.getName()) && 
                productRepository.existsByNameAndBranchId(requestDto.getName(), product.getBranch().getId())) {
                throw new DuplicateEntityException("Product with name '" + requestDto.getName() + "' already exists for this branch");
            }
            
            product.setName(requestDto.getName());
            Product savedProduct = productRepository.save(product);
            
            Product reloadedProduct = productRepository.findByIdWithBranch(savedProduct.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found after save"));
            
            ProductResponseDto result = productMapper.toDto(reloadedProduct);
            log.info("Product name updated successfully - Result: {}", result);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ProductResponseDto> updateProductStock(Long id, UpdateStockRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Updating product stock for ID: {} to: {}", id, requestDto.getStock());
            
            Product product = productRepository.findByIdWithBranch(id)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
            
            product.setStock(requestDto.getStock());
            Product savedProduct = productRepository.save(product);
            
            Product reloadedProduct = productRepository.findByIdWithBranch(savedProduct.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found after save"));
            
            ProductResponseDto result = productMapper.toDto(reloadedProduct);
            log.info("Product stock updated successfully - Result: {}", result);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        return Mono.fromRunnable(() -> {
            log.info("Deleting product with ID: {}", id);
            
            if (!productRepository.existsById(id)) {
                throw new EntityNotFoundException("Product not found with ID: " + id);
            }
            
            productRepository.deleteById(id);
            log.info("Product deleted successfully");
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
} 