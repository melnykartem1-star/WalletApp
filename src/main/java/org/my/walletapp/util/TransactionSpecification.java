package org.my.walletapp.util;

import org.my.walletapp.entity.Transaction;
import org.my.walletapp.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("account").get("user").get("id"), userId);
    }

    public static Specification<Transaction> byCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null ?
                cb.conjunction() :
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Transaction> fromDate(LocalDateTime startDate) {
        return (root, query, cb) -> startDate == null ?
                cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<Transaction> toDate(LocalDateTime endDate) {
        return (root, query, cb) -> endDate == null ?
                cb.conjunction() :
                cb.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

    public static Specification<Transaction> byType(TransactionType type) {
        return (root, query, cb) -> type == null ?
                cb.conjunction() :
                cb.equal(root.get("type"), type);
    }
}