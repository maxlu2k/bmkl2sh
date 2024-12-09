package com.demo.repositories;

import com.demo.entities.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedRepository extends JpaRepository<InvalidatedToken,String> {
}
