package com.example.springai.repository;

import com.example.springai.entity.NFR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFRRepository extends JpaRepository<NFR, Long> {
}
