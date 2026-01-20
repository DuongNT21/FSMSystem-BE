package com.swp391_be.SWP391_be.exception;


public class BadHttpRequestException extends RuntimeException {
    public BadHttpRequestException(String message) {
        super(message);
    }
}
