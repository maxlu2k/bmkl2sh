package com.demo.mappers;

import com.demo.dto.request.PermissionRequest;
import com.demo.dto.response.PermissionResponse;
import com.demo.entities.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
