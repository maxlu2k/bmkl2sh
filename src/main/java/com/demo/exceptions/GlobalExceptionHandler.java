package com.demo.exceptions;

import com.demo.constant.ErrorCode;
import com.demo.dto.response.ErrorHandleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //define các exceptions muốn bắt lỗi ở đây
    @ExceptionHandler(value = RuntimeException.class) //xử lý ex theo class và mỗi khi có RuntimeException bất kì sẽ đều được xử lý tại đây
    ResponseEntity<String> handlingRuntimException(RuntimeException exception){
        return ResponseEntity.badRequest().body(exception.getMessage()); //trả về một Response
    }

    @ExceptionHandler(NullPointerException.class)
    ResponseEntity<ErrorHandleResponse> NullPointerExceptionHandler(NullPointerException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorHandleResponse(HttpStatus.BAD_REQUEST,ErrorCode.NULL_POITER));
    }
}
