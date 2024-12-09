package com.demo.repositories;

import com.demo.entities.InvalidatedToken;
import com.demo.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,String> {
}
