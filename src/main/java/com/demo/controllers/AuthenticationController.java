package com.demo.controllers;


import com.demo.dto.request.AuthenticationRequest;
import com.demo.dto.request.IntrospectRequest;
import com.demo.dto.request.LogoutRequest;
import com.demo.dto.request.RefreshTokenRequest;
import com.demo.dto.request.UpdateAuthorizationRequest;
import com.demo.dto.request.UserRequest;
import com.demo.dto.response.AccessTokenResponse;
import com.demo.dto.response.ApiResponse;
import com.demo.dto.response.AuthenticationResponse;
import com.demo.dto.response.IntrospectResponse;
import com.demo.services.impl.AuthenticationService;
import com.demo.services.impl.UserServiceImpl;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserServiceImpl userService;
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String newAccessToken = authenticationService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
    }

    @PostMapping("/loginn")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        var response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request){
        var response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authorizations")
    public ResponseEntity<?> getAuthorization(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        var response = userService.pageAuthorization(page,size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/authorizations/{userId}")
    public ResponseEntity<?> updateAuthorization(
            @PathVariable Long userId,
            @RequestBody UpdateAuthorizationRequest request) {
        try {
            userService.updateUserRole(userId, request.getRole(), request.getValue());
            return ResponseEntity.ok("Authorization updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
