package com.demo.services.impl;

import com.demo.constant.PreDefinedRole;
import com.demo.dto.request.UserRequest;
import com.demo.dto.response.AuthenticationResponse;
import com.demo.dto.response.AuthorizationResponse;
import com.demo.dto.response.UserResponse;
import com.demo.entities.Role;
import com.demo.entities.User;
import com.demo.mappers.UserMapper;
import com.demo.repositories.RoleRepository;
import com.demo.repositories.UserRepository;
import com.demo.services.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {

        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse addUser(UserRequest request) {
        User user = userMapper.toUser(request);//mapper dto sang entity
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        // kiểm tra role trong bảng ROLE nếu có ROLE USER thì sẽ thêm vào mặc định mỗi khi tạo nếu không có sẽ trả về rỗng tức là roles của đối tượng đó []
        roleRepository.findById(PreDefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Optional<User> userOptional = userRepository.findByUsername(name);
        User user = userOptional.get();
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(Long id, UserRequest userInput) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (userInput.getUsername() != null && !userInput.getUsername().isBlank()) {
            user.setUsername(userInput.getUsername());
        }
        if (userInput.getFirstName() != null && !userInput.getFirstName().isBlank()) {
            user.setFirstName(userInput.getFirstName());
        }
        if (userInput.getLastName() != null && !userInput.getLastName().isBlank()) {
            user.setLastName(userInput.getLastName());
        }
        if (userInput.getEmail() != null && !userInput.getEmail().isBlank()) {
            user.setEmail(userInput.getEmail());
        }
        if (userInput.getPhoneNumber() != null && !userInput.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(userInput.getPhoneNumber());
        }
        if (userInput.getDateOfBirth() != null && !userInput.getDateOfBirth().toString().isBlank()) {
            user.setDateOfBirth(userInput.getDateOfBirth());
        }
        if (userInput.getGender() != null) {
            user.setGender(userInput.getGender());
        }
        if (userInput.getPassword() != null && !userInput.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userInput.getPassword()));
        }
        if (userInput.getIsActive() != null) {
            user.setIsActive(userInput.getIsActive());
        }
        if (userInput.getIsVerify() != null) {
            user.setIsVerify(userInput.getIsVerify());
        }
        // Cập nhật roles nếu được cung cấp và hợp lệ
        if (userInput.getRoles() != null && !userInput.getRoles().isEmpty()) {
            Set<Role> validRoles = new HashSet<>();
            for (Role role : userInput.getRoles()) {
                roleRepository.findById(role.getName()).ifPresent(validRoles::add);
            }
            user.setRoles(validRoles);
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    public UserResponse registerUser(UserRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        Role userRole = roleRepository.findByName(PreDefinedRole.USER_ROLE);
        if (userRole == null) {
            throw new RuntimeException("Không tìm thấy vai trò mặc định USER.");
        }
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (!user.getIsVerify()) {
                // Nếu tài khoản chưa được xác minh, cập nhật thông tin
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setUsername(request.getUsername());
                user.setPhoneNumber(request.getPhoneNumber());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setIsActive(false); // Đặt lại trạng thái chờ xác minh
            } else {
                // Nếu tài khoản đã xác minh, throw exception hoặc xử lý phù hợp
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Email đã được sử dụng và xác minh!");
            }
        } else {
            // Tạo tài khoản mới nếu email chưa tồn tại
            user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .isActive(true)  // Đặt là false, chờ xác minh email
                    .isVerify(false) // Đánh dấu chưa xác minh email
                    .roles(Collections.singleton(userRole))
                    .build();
        }

        User updatedUser = userRepository.save(user);

        // Gửi email xác minh
//        emailService.sendVerificationEmail(updatedUser);

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> pageUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);  // Convert từng user sang response
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AuthorizationResponse> pageAuthorization(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(userMapper::toAuthorization);
    }

    @Override
    public void updateUserRole(Long userId, String roleName, Boolean value) {
        // Lấy thông tin user từ database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy thông tin role từ database
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Cập nhật vai trò
        Set<Role> roles = user.getRoles();
        if (value) {
            // Thêm vai trò
            roles.add(role);
        } else {
            // Xóa vai trò
            roles.remove(role);
        }

        // Lưu lại vào database
        user.setRoles(roles);
        userRepository.save(user);
    }
}
