package com.test.franchise.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "franchises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Franchise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "franchise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 20)
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addBranch(Branch branch) {
        branches.add(branch);
        branch.setFranchise(this);
    }
    
    public void removeBranch(Branch branch) {
        branches.remove(branch);
        branch.setFranchise(null);
    }
} 