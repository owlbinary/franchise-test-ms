package com.test.franchise.controller;

import com.test.franchise.dto.request.BranchRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.ApiResponse;
import com.test.franchise.dto.response.BranchResponseDto;
import com.test.franchise.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Slf4j
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<BranchResponseDto>>> createBranch(@Valid @RequestBody BranchRequestDto request) {
        log.info("POST /api/v1/branches - Creating branch: {} for franchise: {}", request.getName(), request.getFranchiseId());
        return branchService.createBranch(request)
                .map(branch -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(branch, "Branch created successfully")))
                .doOnSuccess(response -> log.info("Branch created successfully"))
                .doOnError(error -> log.error("Error creating branch", error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<BranchResponseDto>>> getBranchById(@PathVariable Long id) {
        log.info("GET /api/v1/branches/{} - Fetching branch", id);
        return branchService.getBranchById(id)
                .map(branch -> ResponseEntity.ok(ApiResponse.success(branch)))
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.info("Branch not found with ID: {}, returning 404", id);
                    return ResponseEntity.notFound().<ApiResponse<BranchResponseDto>>build();
                }))
                .doOnSuccess(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        log.info("Branch fetched successfully");
                    }
                })
                .doOnError(error -> log.error("Error fetching branch", error));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<BranchResponseDto>>>> getBranchesByFranchise(
            @RequestParam(name = "franchiseId") Long franchiseId) {
        log.info("GET /api/v1/branches?franchiseId={} - Fetching branches", franchiseId);
        return branchService.getBranchesByFranchiseId(franchiseId)
                .map(branches -> {
                    log.info("Found {} branches for franchise {}", branches.size(), franchiseId);
                    return ResponseEntity.ok(ApiResponse.success(branches));
                })
                .doOnSuccess(response -> log.info("Branches fetched successfully"))
                .doOnError(error -> log.error("Error fetching branches", error));
    }

    @PutMapping("/{id}/name")
    public Mono<ResponseEntity<ApiResponse<BranchResponseDto>>> updateBranchName(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateNameRequestDto request) {
        log.info("PUT /api/v1/branches/{}/name - Updating branch name to: {}", id, request.getName());
        return branchService.updateBranchName(id, request)
                .map(branch -> ResponseEntity.ok(ApiResponse.success(branch, "Branch name updated successfully")))
                .doOnSuccess(response -> log.info("Branch name updated successfully"))
                .doOnError(error -> log.error("Error updating branch name", error));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteBranch(@PathVariable Long id) {
        log.info("DELETE /api/v1/branches/{} - Deleting branch", id);
        return branchService.deleteBranch(id)
                .map(result -> ResponseEntity.ok(ApiResponse.<Void>success(null, "Branch deleted successfully")))
                .doOnSuccess(response -> log.info("Branch deleted successfully"))
                .doOnError(error -> log.error("Error deleting branch", error));
    }
} 