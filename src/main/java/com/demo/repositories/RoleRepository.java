package com.demo.repositories;

import com.demo.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,String> {
    Role findByName(String name);
}
