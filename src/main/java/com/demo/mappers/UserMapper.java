package com.demo.mappers;

import com.demo.dto.request.UserRequest;
import com.demo.dto.response.UserResponse;
import com.demo.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest request);
    UserResponse toUserResponse(User user);
}
