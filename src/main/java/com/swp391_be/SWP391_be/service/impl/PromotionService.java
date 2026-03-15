package com.swp391_be.SWP391_be.service.impl;

import com.swp391_be.SWP391_be.dto.request.promotion.CreatePromotionRequest;
import com.swp391_be.SWP391_be.dto.request.promotion.UpdatePromotionRequest;
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

        // Check if existing promotion is inside the new promotion's date range
        int overlappingCount = promotionRepository.countOverlappingPromotions(null, request.getStartDate(),
                request.getEndDate());
        if (overlappingCount > 0) {
            throw new BadHttpRequestException("Promotion cannot overlap with existing promotion");
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
        promotion.setActive(request.isStatus());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setActive(request.isStatus());

        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(UpdatePromotionRequest request) {
        Promotion promotion = promotionRepository.findById(request.getId())
                .orElseThrow(() -> new BadHttpRequestException("Promotion not found"));
        if (promotionRepository.existsByCode(request.getCode()) && !promotion.getCode().equals(request.getCode())) {
            throw new BadHttpRequestException("Promotion code already exists");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadHttpRequestException("Start date must be before end date");
        }
        if (request.getDiscountValue() <= 0 || request.getDiscountValue() > 100) {
            throw new BadHttpRequestException("Discount value must be between 0 and 100");
        }

        // Check if existing promotion is inside the new promotion's date range
        int overlappingCount = promotionRepository.countOverlappingPromotions(Long.valueOf(request.getId()),
                request.getStartDate(), request.getEndDate());
        if (overlappingCount > 0) {
            throw new BadHttpRequestException("Promotion cannot overlap with existing promotion");
        }

        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setMinOrderValue(request.getMinOrderValue());
        promotion.setMaxDiscountValue(request.getMaxDiscountValue());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        promotion.setActive(request.isStatus());
        Promotion savedPromotion = promotionRepository.save(promotion);
        return new PromotionResponse(savedPromotion);
    }

    @Override
    public PageResponse<PromotionResponse> getAllPromotions(String name, Boolean isActive, Pageable pageable) {
        Page<Promotion> promotions;
        if (name != null && !name.isBlank() && isActive != null) {
            // Search by keyword and status
            promotions = promotionRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);
        } else if (name != null && !name.isBlank()) {
            // Search by keyword only
            promotions = promotionRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (isActive != null) {
            // Search by status only
            promotions = promotionRepository.findByIsActive(isActive, pageable);
        } else {
            // Search all cases
            promotions = promotionRepository.findAll(pageable);
        }
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
    public PromotionResponse getCurrentPromotion() {
        LocalDate today = LocalDate.now();
        Promotion promotion = promotionRepository.findActivePromotion(today)
                .orElseThrow(() -> new NotFoundException("No active promotion found"));
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
