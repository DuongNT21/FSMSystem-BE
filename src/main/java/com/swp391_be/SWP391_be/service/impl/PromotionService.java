package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.promotion.CreatePromotionRequest;
import com.swp391_be.SWP391_be.dto.response.pageResponse.PageResponse;
import com.swp391_be.SWP391_be.dto.response.promotion.PromotionResponse;
import com.swp391_be.SWP391_be.entity.Promotion;
import com.swp391_be.SWP391_be.exception.BadHttpRequestException;
import com.swp391_be.SWP391_be.exception.NotFoundException;
import com.swp391_be.SWP391_be.repository.PromotionRepository;
import com.swp391_be.SWP391_be.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionService implements IPromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionResponse createPromotion(CreatePromotionRequest request) {
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new BadHttpRequestException("Promotion code already exists");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadHttpRequestException("Start date must be before end date");
        }
        if (request.getDiscountValue() <= 0 || request.getDiscountValue() > 100) {
            throw new BadHttpRequestException("Discount value must be between 0 and 100");
        }

        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setCreatedAt(LocalDateTime.now());

        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(savedPromotion);
    }

    @Override
    public PageResponse<PromotionResponse> getAllPromotions(String name, Pageable pageable) {
        Page<Promotion> promotions = (name != null && !name.isBlank())
                ? promotionRepository.findByNameContainingIgnoreCase(name, pageable)
                : promotionRepository.findAll(pageable);
        return PageResponse.fromPage(promotions, PromotionResponse::new);
    }

    @Override
    public PromotionResponse getPromotionByCode(String code, double orderValue) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));
        LocalDate now = LocalDate.now();
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new BadHttpRequestException("Promotion is expired or not yet active");
        }
        if (orderValue < promotion.getMinOrderValue()) {
            throw new BadHttpRequestException("Order value is not enough to apply this promotion");
        }
        return new PromotionResponse(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promotion not found"));
        promotionRepository.delete(promotion);
    }
}
