package com.swp391_be.SWP391_be.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private Object data;
    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Object data) {
        super(message);
        this.data = data;
    }
}
