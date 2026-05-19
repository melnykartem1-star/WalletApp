package org.my.walletapp.service.category;

import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryResponse> getAllCategories(Pageable pageable, Long userId);
    CategoryResponse createCategory(Long userId, CategoryRequest request);
    CategoryResponse updateCategoryById(Long userId, Long categoryId, CategoryRequest request);
    CategoryResponse getCategoryById(Long userId, Long categoryId);
    void deleteCategoryById(Long userId, Long categoryId);

}
