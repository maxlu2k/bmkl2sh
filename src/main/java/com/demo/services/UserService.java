package com.demo.services;

import com.demo.dto.request.UserRequest;
import com.demo.dto.response.AuthenticationResponse;
import com.demo.dto.response.AuthorizationResponse;
import com.demo.dto.response.UserResponse;
import com.demo.entities.Product;
import com.demo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse addUser(UserRequest request);
    UserResponse getMyInfo();
    void deleteUser(Long id);
    UserResponse updateUser(Long id,UserRequest request);
    UserResponse registerUser(UserRequest request);
    Page<UserResponse> pageUsers(int page, int size);
    Page<AuthorizationResponse> pageAuthorization(int page, int size);
    void updateUserRole(Long userId, String role, Boolean value);
}
