package com.demo.repositories;

import com.demo.entities.BackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlackListTokenRepository extends JpaRepository<BackListToken, UUID> {
    void deleteById(String tokenID);
    boolean existsById(String jwtid);
}
