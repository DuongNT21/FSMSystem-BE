package com.swp391_be.SWP391_be.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.swp391_be.SWP391_be.dto.request.bouquet.GetBouquetCriteriaRequest;
import com.swp391_be.SWP391_be.entity.Bouquet;

import jakarta.persistence.criteria.Predicate;

public class BouquetSpec {

    public static Specification<Bouquet> byCriteria(
            GetBouquetCriteriaRequest criteria
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("name")),
                        "%" + criteria.getName().toLowerCase() + "%"
                    )
                );
            }

            if (criteria.getStatus() != null) {
                predicates.add(
                    cb.equal(root.get("status"), criteria.getStatus())
                );
            }

            if (criteria.getMinPrice() != null) {
                predicates.add(
                    cb.greaterThanOrEqualTo(
                        root.get("price"),
                        criteria.getMinPrice()
                    )
                );
            }

            if (criteria.getMaxPrice() != null) {
                predicates.add(
                    cb.lessThanOrEqualTo(
                        root.get("price"),
                        criteria.getMaxPrice()
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
