package com.brian.transaction_importer_spring.specification;

import com.brian.transaction_importer_spring.dto.TransactionSearchDTO;
import com.brian.transaction_importer_spring.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> searchTransactions(TransactionSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search by description (case-insensitive, partial match)
            if (searchDTO.getDescription() != null && !searchDTO.getDescription().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + searchDTO.getDescription().toLowerCase() + "%"
                ));
            }

            // Search by category name (case-insensitive, partial match)
            if (searchDTO.getCategory() != null && !searchDTO.getCategory().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("category").get("name")),
                        "%" + searchDTO.getCategory().toLowerCase() + "%"
                ));
            }

            // Search by vendor/merchant name (case-insensitive, partial match)
            if (searchDTO.getVendor() != null && !searchDTO.getVendor().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("merchant").get("name")),
                        "%" + searchDTO.getVendor().toLowerCase() + "%"
                ));
            }

            // Search by notes (case-insensitive, partial match)
            if (searchDTO.getNotes() != null && !searchDTO.getNotes().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("notes")),
                        "%" + searchDTO.getNotes().toLowerCase() + "%"
                ));
            }

            // Date range - start date
            if (searchDTO.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("date"),
                        searchDTO.getStartDate()
                ));
            }

            // Date range - end date
            if (searchDTO.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"),
                        searchDTO.getEndDate()
                ));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}