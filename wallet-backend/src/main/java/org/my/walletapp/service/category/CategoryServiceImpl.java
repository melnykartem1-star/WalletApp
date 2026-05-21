package org.my.walletapp.service.category;

import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.entity.Category;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.CategoryMapper;
import org.my.walletapp.repository.CategoryRepository;
import org.my.walletapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements  CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable, Long userId) {
        return categoryRepository.findAllByUserIdAndIsActiveTrue(userId, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);

        User userProxy = userRepository.getReferenceById(userId);
        category.setUser(userProxy);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategoryById(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        categoryMapper.partialUpdate(request, category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        category.setActive(false);
    }
}
