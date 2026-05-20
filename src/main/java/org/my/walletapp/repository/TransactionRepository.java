package org.my.walletapp.repository;

import org.my.walletapp.dto.transaction.TransactionStatisticProjection;
import org.my.walletapp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaSpecificationExecutor<Transaction>, JpaRepository<Transaction, Long> {

    @Query("""
        SELECT c.title AS categoryName, SUM(t.amount) AS amount, c.type AS type
        FROM Transaction t
        LEFT JOIN t.category c
        JOIN t.account a
        WHERE a.user.id = :userId
          AND t.type != org.my.walletapp.enums.TransactionType.TRANSFER -- Відсікаємо трансфери
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND t.createdAt >= :startDate
          AND t.createdAt <= :endDate
        GROUP BY c.id, c.title, c.type
    """)
    List<TransactionStatisticProjection> getStatisticsByPeriod(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Optional<Transaction> findByIdAndAccount_UserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"category", "merchant", "account", "targetAccount"})
    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);

}
