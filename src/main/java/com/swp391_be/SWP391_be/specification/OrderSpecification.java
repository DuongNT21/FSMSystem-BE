package com.swp391_be.SWP391_be.specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.swp391_be.SWP391_be.dto.request.order.GetOrderCriteriaRequest;
import com.swp391_be.SWP391_be.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {
    public static Specification<Order> byCriteria(GetOrderCriteriaRequest criteria) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getFromDate().atStartOfDay()));
            }

            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getToDate().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
