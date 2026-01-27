package com.swp391_be.SWP391_be.exception;

import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthorizationDeniedExceptionHandler {
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Object>> handleException(AuthorizationDeniedException e) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setMessage("Access Denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
