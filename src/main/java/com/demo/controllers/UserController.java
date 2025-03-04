package com.demo.controllers;

import com.demo.dto.request.UserExcel;
import com.demo.dto.request.UserRequest;
import com.demo.dto.response.ApiResponse;
import com.demo.dto.response.UserResponse;
import com.demo.entities.User;
import com.demo.exceptions.GlobalExceptionHandler;
import com.demo.services.UploadService;
import com.demo.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Log4j2
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    private UploadService uploadService;

    @Operation(summary = "Get user",description = "API Get all user")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAll() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        List<UserResponse> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/pageUsers")
    public Page<UserResponse> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return userService.pageUsers(page, size);
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

    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = uploadService.uploadFile(file, "profiles");
            return ResponseEntity.ok().body("{\"message\": \"Profile photo uploaded!\", \"filePath\": \"" + filePath + "\"}");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\": \"Failed to upload profile photo!\"}");
        }
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadFile2(@RequestParam("file") MultipartFile file) throws IOException {
        userService.importExcel(file);
        return ResponseEntity.ok("import success!");
    }

    @GetMapping("/test")
    public ResponseEntity<String> getNUll() {
        try {
            Long x = null;
            x += 2;
        }catch (NullPointerException e){
            System.out.println("error" + e.getMessage());
            throw e;
        }
        return ResponseEntity.ok("import success!");
    }
}
