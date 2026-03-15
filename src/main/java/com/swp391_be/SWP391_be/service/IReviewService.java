package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.review.CreateReviewRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.review.CreateReviewResponse;
import com.swp391_be.SWP391_be.dto.response.review.GetReviewResponse;

public interface IReviewService {
    CreateReviewResponse createReview(int id, CreateReviewRequest request);
    PageResponse<GetReviewResponse> getAllReviews(int id, int page, int size, String sort);
}
