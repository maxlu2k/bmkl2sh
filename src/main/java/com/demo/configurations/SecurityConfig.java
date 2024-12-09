package com.demo.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENPOINT_POST = {
            "/api/users/add","/auth/*", "/auth/token", "/auth/introspect", "/swagger-ui/**","/auth/logout"
    };

    private static final String[] WHITE_LIST_URL = {"/swagger-ui/**","/api-docs/**"};

//    @Value("${jwt.signerKey}")
//    private String signerKey;

    @Autowired
    @Lazy
    private CustomJWTDecoderConfig customJWTDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINT_POST).permitAll()
                        .requestMatchers(WHITE_LIST_URL).permitAll()
//                        .requestMatchers(HttpMethod.GET,"/api/users/**")
//                        .hasAnyRole("ADMIN")
                        .anyRequest().authenticated());

        //cấu hình để khi những enpoint private được truy cập, phải cần đến token để được cấp quyền
        //muốn đăng ký một provider manager để support xử lý cho jwt token
        //oauth2 sẽ sử dụng jwtDecoder đó để thực hiện giải mã token và verify để biết token đó hợp lệ hay ko
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJWTDecoder) //replace jwtDecoder() by CustomJWTDeconder
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()) //thêm prefix convert từ SCOPE_ sang ROLE_
                )
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    //Convert tiền tố của authority
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        //custom authorities Converter
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    //build jwtDecoder
//    @Bean
//    JwtDecoder jwtDecoder() {
//        //tạo một secretKey gồm chữ ký và thuật toán mã hoá
//        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
//        return NimbusJwtDecoder
//                .withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}