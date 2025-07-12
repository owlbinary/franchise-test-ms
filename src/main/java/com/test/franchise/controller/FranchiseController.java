package com.test.franchise.controller;

import com.test.franchise.dto.request.FranchiseRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.response.ApiResponse;
import com.test.franchise.dto.response.FranchiseResponseDto;
import com.test.franchise.dto.response.TopStockProductResponseDto;
import com.test.franchise.service.FranchiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
@Slf4j
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<FranchiseResponseDto>>> createFranchise(@Valid @RequestBody FranchiseRequestDto request) {
        log.info("POST /api/v1/franchises - Creating franchise: {}", request.getName());
        return franchiseService.createFranchise(request)
                .map(franchise -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(franchise, "Franchise created successfully")))
                .doOnSuccess(response -> log.info("Franchise created successfully"))
                .doOnError(error -> log.error("Error creating franchise", error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<FranchiseResponseDto>>> getFranchiseById(@PathVariable Long id) {
        log.info("GET /api/v1/franchises/{} - Fetching franchise", id);
        return franchiseService.getFranchiseById(id)
                .map(franchise -> ResponseEntity.ok(ApiResponse.success(franchise)))
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.info("Franchise not found with ID: {}, returning 404", id);
                    return ResponseEntity.notFound().<ApiResponse<FranchiseResponseDto>>build();
                }))
                .doOnSuccess(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        log.info("Franchise fetched successfully");
                    }
                })
                .doOnError(error -> log.error("Error fetching franchise", error));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<FranchiseResponseDto>>>> getAllFranchises() {
        log.info("GET /api/v1/franchises - Fetching all franchises");
        return franchiseService.getAllFranchises()
                .map(franchises -> {
                    log.info("Found {} franchises", franchises.size());
                    return ResponseEntity.ok(ApiResponse.success(franchises));
                })
                .doOnSuccess(response -> log.info("All franchises fetched successfully"))
                .doOnError(error -> log.error("Error fetching franchises", error));
    }

    @PutMapping("/{id}/name")
    public Mono<ResponseEntity<ApiResponse<FranchiseResponseDto>>> updateFranchiseName(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateNameRequestDto request) {
        log.info("PUT /api/v1/franchises/{}/name - Updating franchise name to: {}", id, request.getName());
        return franchiseService.updateFranchiseName(id, request)
                .map(franchise -> ResponseEntity.ok(ApiResponse.success(franchise, "Franchise name updated successfully")))
                .doOnSuccess(response -> log.info("Franchise name updated successfully"))
                .doOnError(error -> log.error("Error updating franchise name", error));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteFranchise(@PathVariable Long id) {
        log.info("DELETE /api/v1/franchises/{} - Deleting franchise", id);
        return franchiseService.deleteFranchise(id)
                .map(result -> ResponseEntity.ok(ApiResponse.<Void>success(null, "Franchise deleted successfully")))
                .doOnSuccess(response -> log.info("Franchise deleted successfully"))
                .doOnError(error -> log.error("Error deleting franchise", error));
    }

    @GetMapping("/{id}/top-stock-products")
    public Mono<ResponseEntity<ApiResponse<TopStockProductResponseDto>>> getTopStockProducts(@PathVariable Long id) {
        log.info("GET /api/v1/franchises/{}/top-stock-products - Fetching top stock products", id);
        return franchiseService.getTopStockProductsByFranchise(id)
                .map(result -> {
                    log.info("Found {} top products for franchise {}", 
                            result.getBranchTopProducts().size(), id);
                    return ResponseEntity.ok(ApiResponse.success(result));
                })
                .doOnSuccess(response -> log.info("Top stock products fetched successfully"))
                .doOnError(error -> log.error("Error fetching top stock products", error));
    }
} 