package com.demo.dto.request;

import com.demo.entities.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserExcel {
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    Boolean gender;
    Date dateOfBirth;
    Set<Role> roles;
    Boolean isActive;
    Boolean isVerify;
}
