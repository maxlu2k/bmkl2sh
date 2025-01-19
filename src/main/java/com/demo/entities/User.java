package com.demo.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    Boolean gender;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;
    Boolean isVerify;
    @ManyToMany // Nhiều tài khoản có thể có nhiều vai trò
    Set<Role> roles;

    @Column(name = "is_active")
    Boolean isActive; // Trạng thái tài khoản (VD: có hoạt động hay không)

}
//fetch = FetchType.EAGER:
//  FetchType.EAGER nghĩa là khi bạn tải một đối tượng User từ cơ sở dữ liệu,
//      Hibernate sẽ tự động tải tất cả các Role liên quan đến đối tượng đó ngay lập tức.
//      Điều này rất hữu ích nếu bạn muốn sử dụng vai trò ngay lập tức sau khi truy vấn tài khoản.
//  Lưu ý: EAGER có thể làm chậm ứng dụng nếu số lượng Role lớn hoặc nếu bạn không thực sự cần tất cả vai trò khi tải đối tượng.
//      Nếu không cần tải ngay vai trò, bạn có thể thay thế bằng FetchType.LAZY để chỉ tải khi thực sự cần.