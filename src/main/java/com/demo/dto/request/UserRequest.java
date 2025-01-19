package com.demo.dto.request;

import com.demo.entities.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    long id;
    String username;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String password;
    Boolean gender;
    Date dateOfBirth;
    Set<Role> roles;
    Boolean isActive;
    Boolean isVerify;
}
