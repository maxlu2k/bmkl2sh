package com.demo.configurations;

import com.demo.constant.PreDefinedRole;
import com.demo.entities.Role;
import com.demo.entities.User;
import com.demo.repositories.RoleRepository;
import com.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    //sẽ được gọi mỗi khi start server
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            //kiểm tra xem có role user chưa -- chưa có thì add vào
            Role userRole = roleRepository.findByName(PreDefinedRole.USER_ROLE);
            if (userRole == null) {
                userRole = Role.builder()
                        .name(PreDefinedRole.USER_ROLE)
                        .description("khach hang")
                        .build();
                roleRepository.save(userRole);
            }
            Role adminRole = roleRepository.findByName(PreDefinedRole.ADMIN_ROLE);
            //kiểm tra xem có role admin chưa -- chưa có thì add vào
            if (adminRole == null) {
                adminRole = Role.builder()
                        .name(PreDefinedRole.ADMIN_ROLE)
                        .description("quan ly")
                        .build();
                roleRepository.save(adminRole);
            }
            //kiểm tra xem có người dùng admin có role admin chưa nếu chưa thì thêm người dùng có role admin vào
            if (userRepository.findByUsername("admin").isEmpty()) {
                //Tạo user mới có admin
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .isActive(true)
                        .email("manhdung6112000@gmail.com")
                        .roles(new HashSet<>(Collections.singletonList(adminRole))) //Collections.singletonList phương thức trả về danh sách chứa phần tử adminRole không trùng lặp
                        .build();
                userRepository.save(user);
            }
        };
    }
}
