package com.demo.services;

import com.demo.dto.request.UserRequest;
import com.demo.dto.response.UserResponse;
import com.demo.entities.Product;
import com.demo.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse addUser(UserRequest request);
    UserResponse getMyInfo();
    void deleteUser(Long id);
    UserResponse updateUser(Long id,UserRequest request);
}
