package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.review.CreateReviewRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.review.CreateReviewResponse;
import com.swp391_be.SWP391_be.dto.response.review.GetReviewResponse;
import com.swp391_be.SWP391_be.entity.Bouquet;
import com.swp391_be.SWP391_be.entity.Review;
import com.swp391_be.SWP391_be.entity.User;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.BouquetRepository;
import com.swp391_be.SWP391_be.repository.ReviewRepository;
import com.swp391_be.SWP391_be.repository.UserRepository;
import com.swp391_be.SWP391_be.service.IReviewService;
import com.swp391_be.SWP391_be.util.AuthenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final BouquetRepository bouquetRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public CreateReviewResponse createReview(int id, CreateReviewRequest request) {
        int userId = AuthenUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Bouquet bouquet = bouquetRepository.findById(id).orElseThrow(() -> new RuntimeException("Bouquet not found"));
        Review review = new Review();
        review.setBouquet(bouquet);
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setUser(user);
        reviewRepository.save(review);
        CreateReviewResponse response = new CreateReviewResponse();
        response.setTitle(review.getTitle());
        response.setContent(review.getContent());
        response.setRating(review.getRating());
        return response;
    }

    @Override
    public PageResponse<GetReviewResponse> getAllReviews(int id, int page, int size, String sort) {
        Bouquet bouquet = bouquetRepository.findById(id).orElseThrow(() -> new RuntimeException("Bouquet not found"));


        Pageable pageable = PageRequest.of(page, size);

        Page<Review> reviews = reviewRepository.getAllReviews(bouquet.getId(), sort, pageable);

        return PageResponse.fromPage(reviews, review -> {
            GetReviewResponse res = new GetReviewResponse();
            res.setId(review.getId());
            res.setTitle(review.getTitle());
            res.setContent(review.getContent());
            res.setRating(review.getRating());
            res.setBouquetId(review.getId());
            return res;
        });
    }
}
