package com.demo.services;

import com.demo.dto.request.RoleRequest;
import com.demo.dto.request.UserRequest;
import com.demo.dto.response.RoleResponse;
import com.demo.dto.response.UserResponse;
import com.demo.entities.User;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAll();
    RoleResponse createRole(RoleRequest request);
    void deleteRole(String name);
    RoleResponse updateRole(String name, RoleRequest request);
}
