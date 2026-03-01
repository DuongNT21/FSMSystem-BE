package com.swp391_be.SWP391_be.specification;

import java.util.ArrayList;
import java.util.List;

import com.swp391_be.SWP391_be.dto.request.rawMaterial.GetRawMaterialCriteriaRequest;
import org.springframework.data.jpa.domain.Specification;

import com.swp391_be.SWP391_be.entity.RawMaterial;

import jakarta.persistence.criteria.Predicate;

public class RawMaterialSpec {

    public static Specification<RawMaterial> byCriteria(
            GetRawMaterialCriteriaRequest criteria
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

//            if (criteria.getMinQuantity() != null) {
//                predicates.add(cb.greaterThanOrEqualTo(root.get("quantity"), criteria.getMinQuantity()));
//            }
//
//            if (criteria.getMaxQuantity() != null) {
//                predicates.add(cb.lessThanOrEqualTo(root.get("quantity"), criteria.getMaxQuantity()));
//            }

//            if (criteria.getMinImportPrice() != null) {
//                predicates.add(cb.greaterThanOrEqualTo(root.get("importPrice"), criteria.getMinImportPrice()));
//            }
//
//            if (criteria.getMaxImportPrice() != null) {
//                predicates.add(cb.lessThanOrEqualTo(root.get("importPrice"), criteria.getMaxImportPrice())
//                );
//            }

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
