package com.test.franchise.service.impl;

import com.test.franchise.domain.Branch;
import com.test.franchise.domain.Franchise;
import com.test.franchise.dto.request.BranchRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.BranchResponseDto;
import com.test.franchise.exception.DuplicateEntityException;
import com.test.franchise.exception.EntityNotFoundException;
import com.test.franchise.mapper.BranchMapper;
import com.test.franchise.repository.BranchRepository;
import com.test.franchise.repository.FranchiseRepository;
import com.test.franchise.service.BranchService;
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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    private final BranchMapper branchMapper;

    @Override
    public Mono<BranchResponseDto> createBranch(BranchRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Creating branch with name: {} for franchise ID: {}", requestDto.getName(), requestDto.getFranchiseId());
            
            Franchise franchise = franchiseRepository.findById(requestDto.getFranchiseId())
                    .orElseThrow(() -> new EntityNotFoundException("Franchise not found with ID: " + requestDto.getFranchiseId()));
            
            if (branchRepository.existsByNameAndFranchiseId(requestDto.getName(), requestDto.getFranchiseId())) {
                throw new DuplicateEntityException("Branch with name '" + requestDto.getName() + "' already exists for this franchise");
            }
            
            Branch branch = branchMapper.toEntity(requestDto);
            branch.setFranchise(franchise);
            Branch savedBranch = branchRepository.save(branch);
            
            Branch reloadedBranch = branchRepository.findByIdWithFranchise(savedBranch.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found after save"));
            
            BranchResponseDto result = branchMapper.toDtoWithoutProducts(reloadedBranch);
            log.info("Branch created successfully with ID: {}", savedBranch.getId());
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BranchResponseDto> getBranchById(Long id) {
        return Mono.fromCallable(() -> {
            log.info("Fetching branch with ID: {}", id);
            
            Optional<Branch> branchOpt = branchRepository.findByIdWithFranchiseAndProducts(id);
            
            if (branchOpt.isEmpty()) {
                log.info("Branch not found with ID: {}", id);
                return null;
            }
            
            Branch branch = branchOpt.get();
            BranchResponseDto result = branchMapper.toDto(branch);
            log.info("Branch fetched successfully with ID: {}", id);
            return result;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .filter(result -> result != null);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<List<BranchResponseDto>> getBranchesByFranchiseId(Long franchiseId) {
        return Mono.fromCallable(() -> {
            log.info("Fetching branches for franchise ID: {}", franchiseId);
            List<Branch> branches = branchRepository.findByFranchiseIdWithFranchise(franchiseId);
            
            if (branches.isEmpty()) {
                log.info("No branches found for franchise ID: {}", franchiseId);
                return List.<BranchResponseDto>of();
            }
            
            List<BranchResponseDto> result = branches.stream()
                    .map(branchMapper::toDtoWithoutProducts)
                    .toList();
                    
            log.info("Successfully fetched {} branches for franchise ID: {}", result.size(), franchiseId);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<BranchResponseDto> updateBranchName(Long id, UpdateNameRequestDto requestDto) {
        return Mono.fromCallable(() -> {
            log.info("Updating branch name for ID: {} to: {}", id, requestDto.getName());
            
            Branch branch = branchRepository.findByIdWithFranchise(id)
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + id));
            
            if (!branch.getName().equals(requestDto.getName()) && 
                branchRepository.existsByNameAndFranchiseId(requestDto.getName(), branch.getFranchise().getId())) {
                throw new DuplicateEntityException("Branch with name '" + requestDto.getName() + "' already exists for this franchise");
            }
            
            branch.setName(requestDto.getName());
            Branch savedBranch = branchRepository.save(branch);
            
            Branch reloadedBranch = branchRepository.findByIdWithFranchise(savedBranch.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found after save"));
            
            BranchResponseDto result = branchMapper.toDtoWithoutProducts(reloadedBranch);
            log.info("Branch name updated successfully");
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteBranch(Long id) {
        return Mono.fromRunnable(() -> {
            log.info("Deleting branch with ID: {}", id);
            
            if (!branchRepository.existsById(id)) {
                throw new EntityNotFoundException("Branch not found with ID: " + id);
            }
            
            branchRepository.deleteById(id);
            log.info("Branch deleted successfully");
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
} 