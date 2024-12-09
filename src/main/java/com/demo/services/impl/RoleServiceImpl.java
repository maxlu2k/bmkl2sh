package com.demo.services.impl;

import com.demo.dto.request.RoleRequest;
import com.demo.dto.response.RoleResponse;
import com.demo.mappers.RoleMapper;
import com.demo.repositories.PermissionRepository;
import com.demo.repositories.RoleRepository;
import com.demo.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public void deleteRole(String name) {
        roleRepository.deleteById(name);
    }

    @Override
    public RoleResponse updateRole(String name, RoleRequest request) {
        return null;
    }
}
