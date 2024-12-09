package com.demo.exceptions;

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

}
