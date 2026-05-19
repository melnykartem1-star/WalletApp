package org.my.walletapp.repository;

import org.my.walletapp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
    Optional<Category> findByIdAndUserIdAndIsActiveTrue(Long categoryId, Long userId);

}
