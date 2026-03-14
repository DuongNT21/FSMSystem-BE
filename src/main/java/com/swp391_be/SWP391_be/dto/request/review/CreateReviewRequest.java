package com.swp391_be.SWP391_be.dto.request.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateReviewRequest {
    private String title;
    private String content;
    private float rating;
}
