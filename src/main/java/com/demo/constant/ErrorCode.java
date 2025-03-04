package com.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    NULL_POITER("ERR1001","Err null value"),
    NUMBER_FORMAT("ERR1002","Err number format"),
    CLASS_CAST("ERR1003","Err class cast");

    private String code;
    private String message;
}
