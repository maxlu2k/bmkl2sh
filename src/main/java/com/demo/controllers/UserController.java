package com.demo.controllers;

import com.demo.dto.request.UserRequest;
import com.demo.dto.response.ApiResponse;
import com.demo.dto.response.UserResponse;
import com.demo.entities.Product;
import com.demo.entities.User;
import com.demo.services.ProductService;
import com.demo.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Log4j2
public class UserController {
    @Autowired
    UserServiceImpl userService;


    @Operation(summary = "Get user",description = "API Get all user")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        List<UserResponse> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @Operation(summary = "Add user",description = "API Add more user")
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody UserRequest request) {
        UserResponse createUser = userService.addUser(request);
        return new ResponseEntity(createUser, HttpStatus.CREATED);
        //create a new ResponseEntity and return an object createProd with status 201 CREATED
    }

    @Operation(summary = "Update user",description = "API update for user")
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        UserResponse createUser = userService.updateUser(id,request);
        return new ResponseEntity(createUser, HttpStatus.CREATED);
    }
}
