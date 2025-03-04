package com.demo.dto.response;

import com.demo.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorHandleResponse {
    int status;
    String code;
    String message;

    public ErrorHandleResponse(HttpStatus status, ErrorCode errorCode) {
        this.status = status.value();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
