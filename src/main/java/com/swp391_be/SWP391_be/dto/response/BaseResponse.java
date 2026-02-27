package com.swp391_be.SWP391_be.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;
}
