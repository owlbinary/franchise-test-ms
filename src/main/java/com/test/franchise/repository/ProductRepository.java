package com.test.franchise.repository;

import com.test.franchise.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByBranchId(Long branchId);
    
    boolean existsByNameAndBranchId(String name, Long branchId);
    
    @Query("SELECT p FROM Product p WHERE p.branch.franchise.id = :franchiseId")
    List<Product> findByFranchiseId(@Param("franchiseId") Long franchiseId);
    
    @Query("""
        SELECT p FROM Product p 
        JOIN FETCH p.branch b 
        WHERE b.franchise.id = :franchiseId 
        AND p.stock = (
            SELECT MAX(p2.stock) 
            FROM Product p2 
            WHERE p2.branch.id = p.branch.id
        )
        """)
    List<Product> findTopStockProductsByFranchiseId(@Param("franchiseId") Long franchiseId);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.branch WHERE p.id = :id")
    Optional<Product> findByIdWithBranch(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.branch WHERE p.branch.id = :branchId")
    List<Product> findByBranchIdWithBranch(@Param("branchId") Long branchId);
} 