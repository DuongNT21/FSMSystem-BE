package com.swp391_be.SWP391_be.service;

import com.swp391_be.SWP391_be.dto.request.promotion.CreatePromotionRequest;
import com.swp391_be.SWP391_be.dto.request.promotion.UpdatePromotionRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.promotion.PromotionResponse;
import com.swp391_be.SWP391_be.entity.Promotion;

import org.springframework.data.domain.Pageable;

public interface IPromotionService {
    PromotionResponse createPromotion(CreatePromotionRequest request);
    PromotionResponse updatePromotion(UpdatePromotionRequest request);
    PageResponse<PromotionResponse> getAllPromotions(String name,Boolean status, Pageable pageable);
    PromotionResponse getPromotionByCode(String code, double orderValue);
    void deletePromotion(Integer id);
    PromotionResponse getCurrentPromotion();
}