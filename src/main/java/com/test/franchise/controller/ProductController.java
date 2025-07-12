package com.test.franchise.controller;

import com.test.franchise.dto.request.ProductRequestDto;
import com.test.franchise.dto.request.UpdateNameRequestDto;
import com.test.franchise.dto.request.UpdateStockRequestDto;
import com.test.franchise.dto.response.ApiResponse;
import com.test.franchise.dto.response.ProductResponseDto;
import com.test.franchise.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<ProductResponseDto>>> createProduct(@Valid @RequestBody ProductRequestDto request) {
        log.info("POST /api/v1/products - Creating product: {}", request.getName());
        return productService.createProduct(request)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(product, "Product created successfully")))
                .doOnSuccess(response -> log.info("Product created successfully"))
                .doOnError(error -> log.error("Error creating product", error));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<ProductResponseDto>>> getProductById(@PathVariable Long id) {
        log.info("GET /api/v1/products/{} - Fetching product", id);
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product)))
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.info("Product not found with ID: {}, returning 404", id);
                    return ResponseEntity.notFound().<ApiResponse<ProductResponseDto>>build();
                }))
                .doOnSuccess(response -> {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        log.info("Product fetched successfully");
                    }
                })
                .doOnError(error -> log.error("Error fetching product", error));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<ProductResponseDto>>>> getProductsByBranch(
            @RequestParam(name = "branchId") Long branchId) {
        log.info("GET /api/v1/products?branchId={} - Fetching products", branchId);
        return productService.getProductsByBranchId(branchId)
                .map(products -> {
                    log.info("Found {} products for branch {}", products.size(), branchId);
                    return ResponseEntity.ok(ApiResponse.success(products));
                })
                .doOnSuccess(response -> log.info("Products fetched successfully"))
                .doOnError(error -> log.error("Error fetching products", error));
    }

    @PutMapping("/{id}/name")
    public Mono<ResponseEntity<ApiResponse<ProductResponseDto>>> updateProductName(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateNameRequestDto request) {
        log.info("PUT /api/v1/products/{}/name - Updating product name to: {}", id, request.getName());
        return productService.updateProductName(id, request)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product, "Product name updated successfully")))
                .doOnSuccess(response -> log.info("Product name updated successfully"))
                .doOnError(error -> log.error("Error updating product name", error));
    }

    @PutMapping("/{id}/stock")
    public Mono<ResponseEntity<ApiResponse<ProductResponseDto>>> updateProductStock(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateStockRequestDto request) {
        log.info("PUT /api/v1/products/{}/stock - Updating product stock to: {}", id, request.getStock());
        return productService.updateProductStock(id, request)
                .map(product -> ResponseEntity.ok(ApiResponse.success(product, "Product stock updated successfully")))
                .doOnSuccess(response -> log.info("Product stock updated successfully"))
                .doOnError(error -> log.error("Error updating product stock", error));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{} - Deleting product", id);
        return productService.deleteProduct(id)
                .map(result -> ResponseEntity.ok(ApiResponse.<Void>success(null, "Product deleted successfully")))
                .doOnSuccess(response -> log.info("Product deleted successfully"))
                .doOnError(error -> log.error("Error deleting product", error));
    }
} 