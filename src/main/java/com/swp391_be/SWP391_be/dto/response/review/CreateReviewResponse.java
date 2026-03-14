package com.swp391_be.SWP391_be.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateReviewResponse {
    private String title;
    private String content;
    private float rating;
}
