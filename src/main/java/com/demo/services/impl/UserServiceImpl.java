package com.demo.services.impl;

import com.demo.constant.PreDefinedRole;
import com.demo.dto.request.UserRequest;
import com.demo.dto.response.UserResponse;
import com.demo.entities.Role;
import com.demo.entities.User;
import com.demo.mappers.UserMapper;
import com.demo.repositories.RoleRepository;
import com.demo.repositories.UserRepository;
import com.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if (userInput.getEmail() != null && !userInput.getEmail().isBlank()) {
            user.setEmail(userInput.getEmail());
        }
        if (userInput.getPassword() != null && !userInput.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userInput.getPassword()));
        }
        if (userInput.getIsActive() != null) {
            user.setIsActive(userInput.getIsActive());
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

}
