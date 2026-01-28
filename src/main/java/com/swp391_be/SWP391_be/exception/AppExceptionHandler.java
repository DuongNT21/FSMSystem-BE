package com.swp391_be.SWP391_be.exception;

import com.swp391_be.SWP391_be.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ResponseEntity<BaseResponse<Object>> handleException(AppException e) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(e.getMessage());
        response.setData(e.getData());
        return ResponseEntity.badRequest().body(response);
    }
}
