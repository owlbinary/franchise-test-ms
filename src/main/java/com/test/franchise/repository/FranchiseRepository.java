package com.test.franchise.repository;

import com.test.franchise.domain.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {
    
    Optional<Franchise> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT f FROM Franchise f LEFT JOIN FETCH f.branches WHERE f.id = :id")
    Optional<Franchise> findByIdWithBranches(@Param("id") Long id);
    
    @Query("SELECT DISTINCT f FROM Franchise f LEFT JOIN FETCH f.branches")
    List<Franchise> findAllWithBranches();
} 