package com.demo.mappers;

import com.demo.dto.request.RoleRequest;
import com.demo.dto.response.RoleResponse;
import com.demo.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// đánh dấu bean quản lý như một spring nghĩa là có thể autowired hay roleMapper vào bất cứ đâu
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
        //permissions sẽ được loại bỏ không được ánh xạ
    Role toRole(RoleRequest request); //ánh xạ DTO RoleRequest sang entity Role

    RoleResponse toRoleResponse(Role role); // map từ Role sang DTO RoleResponse
}
