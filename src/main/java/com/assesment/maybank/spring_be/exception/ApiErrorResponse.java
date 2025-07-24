package com.assesment.maybank.spring_be.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String error;
    private String message;
    private int code;
    private ZonedDateTime timestamp;
}
