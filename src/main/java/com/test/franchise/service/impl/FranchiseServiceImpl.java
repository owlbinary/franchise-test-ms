package com.test.franchise.service.impl;

import com.test.franchise.domain.Franchise;
import com.test.franchise.domain.Product;
import com.test.franchise.dto.request.FranchiseRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.FranchiseResponseDto;
import com.test.franchise.dto.response.TopStockProductResponseDto;
import com.test.franchise.exception.DuplicateEntityException;
import com.test.franchise.exception.EntityNotFoundException;
import com.test.franchise.mapper.FranchiseMapper;
import com.test.franchise.repository.FranchiseRepository;
import com.test.franchise.repository.ProductRepository;
import com.test.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;
    private final FranchiseMapper franchiseMapper;

    @Override
    public Mono<FranchiseResponseDto> createFranchise(FranchiseRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Creating franchise with name: {}", requestDto.getName());
            
            if (franchiseRepository.existsByName(requestDto.getName())) {
                throw new DuplicateEntityException("Franchise with name '" + requestDto.getName() + "' already exists");
            }
            
            Franchise franchise = franchiseMapper.toEntity(requestDto);
            Franchise savedFranchise = franchiseRepository.save(franchise);
            
            log.info("Franchise created successfully with ID: {}", savedFranchise.getId());
            return franchiseMapper.toDtoWithoutBranches(savedFranchise);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<FranchiseResponseDto> getFranchiseById(Long id) {
        return Mono.fromCallable(() -> {
            log.info("Fetching franchise with ID: {}", id);
            
            Optional<Franchise> franchiseOpt = franchiseRepository.findByIdWithBranches(id);
            
            if (franchiseOpt.isEmpty()) {
                log.info("Franchise not found with ID: {}", id);
                return null;
            }
            
            Franchise franchise = franchiseOpt.get();
            FranchiseResponseDto result = franchiseMapper.toDtoWithBranchesButWithoutProducts(franchise);
            log.info("Franchise fetched successfully with ID: {}", id);
            return result;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .filter(result -> result != null);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<List<FranchiseResponseDto>> getAllFranchises() {
        return Mono.fromCallable(() -> {
            log.info("Fetching all franchises");
            try {
                List<Franchise> franchises = franchiseRepository.findAllWithBranches();
                List<FranchiseResponseDto> result = franchises.stream()
                        .map(franchiseMapper::toDtoWithBranchesButWithoutProducts)
                        .toList();
                log.info("Successfully fetched {} franchises with branches", result.size());
                return result;
            } catch (Exception e) {
                log.error("Error fetching franchises with branches, falling back to basic fetch", e);
                List<Franchise> franchises = franchiseRepository.findAll();
                List<FranchiseResponseDto> result = franchises.stream()
                        .map(franchiseMapper::toDtoWithoutBranches)
                        .toList();
                log.info("Successfully fetched {} franchises without branches", result.size());
                return result;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<FranchiseResponseDto> updateFranchiseName(Long id, UpdateNameRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Updating franchise name for ID: {} to: {}", id, requestDto.getName());
            
            Franchise franchise = franchiseRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Franchise not found with ID: " + id));
            
            if (!franchise.getName().equals(requestDto.getName()) && 
                franchiseRepository.existsByName(requestDto.getName())) {
                throw new DuplicateEntityException("Franchise with name '" + requestDto.getName() + "' already exists");
            }
            
            franchise.setName(requestDto.getName());
            Franchise savedFranchise = franchiseRepository.save(franchise);
            
            log.info("Franchise name updated successfully");
            return franchiseMapper.toDtoWithoutBranches(savedFranchise);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteFranchise(Long id) {
        return Mono.fromRunnable(() -> {
            log.info("Deleting franchise with ID: {}", id);
            
            if (!franchiseRepository.existsById(id)) {
                throw new EntityNotFoundException("Franchise not found with ID: " + id);
            }
            
            franchiseRepository.deleteById(id);
            log.info("Franchise deleted successfully");
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TopStockProductResponseDto> getTopStockProductsByFranchise(Long franchiseId) {
        return Mono.fromCallable(() -> {
            log.info("Fetching top stock products for franchise ID: {}", franchiseId);
            
            Optional<Franchise> franchiseOpt = franchiseRepository.findById(franchiseId);
            
            if (franchiseOpt.isEmpty()) {
                log.info("Franchise not found with ID: {}, returning empty result", franchiseId);
                return TopStockProductResponseDto.builder()
                        .franchiseId(franchiseId)
                        .franchiseName("Unknown")
                        .branchTopProducts(List.of())
                        .build();
            }
            
            Franchise franchise = franchiseOpt.get();
            List<Product> topProducts = productRepository.findTopStockProductsByFranchiseId(franchiseId);
            
            if (topProducts.isEmpty()) {
                log.info("No products found for franchise ID: {}", franchiseId);
                return TopStockProductResponseDto.builder()
                        .franchiseId(franchise.getId())
                        .franchiseName(franchise.getName())
                        .branchTopProducts(List.of())
                        .build();
            }
            
            List<TopStockProductResponseDto.BranchTopProduct> branchTopProducts = topProducts.stream()
                    .map(product -> TopStockProductResponseDto.BranchTopProduct.builder()
                            .branchId(product.getBranch().getId())
                            .branchName(product.getBranch().getName())
                            .productId(product.getId())
                            .productName(product.getName())
                            .stock(product.getStock())
                            .build())
                    .toList();
            
            log.info("Found {} top stock products for franchise: {}", branchTopProducts.size(), franchise.getName());
            return TopStockProductResponseDto.builder()
                    .franchiseId(franchise.getId())
                    .franchiseName(franchise.getName())
                    .branchTopProducts(branchTopProducts)
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }
} 