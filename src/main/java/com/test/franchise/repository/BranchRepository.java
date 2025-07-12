package com.test.franchise.repository;

import com.test.franchise.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    List<Branch> findByFranchiseId(Long franchiseId);
    
    boolean existsByNameAndFranchiseId(String name, Long franchiseId);
    
    @Query("SELECT b FROM Branch b LEFT JOIN FETCH b.franchise WHERE b.id = :id")
    Optional<Branch> findByIdWithFranchise(@Param("id") Long id);
    
    @Query("SELECT b FROM Branch b LEFT JOIN FETCH b.franchise LEFT JOIN FETCH b.products WHERE b.id = :id")
    Optional<Branch> findByIdWithFranchiseAndProducts(@Param("id") Long id);

    @Query("SELECT b FROM Branch b LEFT JOIN FETCH b.franchise WHERE b.franchise.id = :franchiseId")
    List<Branch> findByFranchiseIdWithFranchise(@Param("franchiseId") Long franchiseId);
    
} 