package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.entity.Category;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.CategoryType;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.CategoryMapper;
import org.my.walletapp.repository.CategoryRepository;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.service.category.CategoryServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category testCategory;
    private final Long userId = 1L;
    private final Long categoryId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testCategory = new Category();
        testCategory.setId(categoryId);
        testCategory.setTitle("Groceries");
        testCategory.setType(CategoryType.EXPENSE);
        testCategory.setActive(true);
        testCategory.setUser(testUser);
    }

    @Nested
    class GetAllCategoriesTests {

        @Test
        void getAllCategories_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Category> categoryPage = new PageImpl<>(List.of(testCategory));
            CategoryResponse mockResponse = new CategoryResponse(categoryId, "Groceries", null, CategoryType.EXPENSE, true, null, null);

            when(categoryRepository.findAllByUserIdAndIsActiveTrue(userId, pageable)).thenReturn(categoryPage);
            when(categoryMapper.toResponse(testCategory)).thenReturn(mockResponse);

            Page<CategoryResponse> result = categoryService.getAllCategories(pageable, userId);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Groceries", result.getContent().getFirst().title());
            verify(categoryRepository, times(1)).findAllByUserIdAndIsActiveTrue(userId, pageable);
        }
    }

    @Nested
    class CreateCategoryTests {

        @Test
        void createCategory_Success() {
            CategoryRequest request = new CategoryRequest("New Category", "Desc", CategoryType.INCOME, null, null);
            Category mappedCategory = new Category();
            mappedCategory.setTitle("New Category");
            CategoryResponse mockResponse = new CategoryResponse(11L, "New Category", "Desc", CategoryType.INCOME, true, null, null);

            when(categoryMapper.toEntity(request)).thenReturn(mappedCategory);
            when(userRepository.getReferenceById(userId)).thenReturn(testUser);
            when(categoryRepository.save(mappedCategory)).thenReturn(mappedCategory);
            when(categoryMapper.toResponse(mappedCategory)).thenReturn(mockResponse);

            CategoryResponse result = categoryService.createCategory(userId, request);

            assertNotNull(result);
            assertEquals("New Category", result.title());
            assertEquals(testUser, mappedCategory.getUser());
            verify(categoryRepository, times(1)).save(mappedCategory);
        }
    }

    @Nested
    class UpdateCategoryTests {

        @Test
        void updateCategoryById_Success() {
            CategoryRequest request = new CategoryRequest("Updated Groceries", null, null, null, null);
            CategoryResponse mockResponse = new CategoryResponse(categoryId, "Updated Groceries", null, CategoryType.EXPENSE, true, null, null);

            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.of(testCategory));
            when(categoryMapper.toResponse(testCategory)).thenReturn(mockResponse);

            CategoryResponse result = categoryService.updateCategoryById(userId, categoryId, request);

            assertNotNull(result);
            assertEquals("Updated Groceries", result.title());
            verify(categoryMapper, times(1)).partialUpdate(request, testCategory);
        }

        @Test
        void updateCategoryById_ThrowsResourceNotFoundException() {
            CategoryRequest request = new CategoryRequest("Updated Groceries", null, null, null, null);

            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryById(userId, categoryId, request));
            verify(categoryMapper, never()).partialUpdate(any(), any());
        }
    }

    @Nested
    class GetCategoryTests {

        @Test
        void getCategoryById_Success() {
            CategoryResponse mockResponse = new CategoryResponse(categoryId, "Groceries", null, CategoryType.EXPENSE, true, null, null);

            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.of(testCategory));
            when(categoryMapper.toResponse(testCategory)).thenReturn(mockResponse);

            CategoryResponse result = categoryService.getCategoryById(userId, categoryId);

            assertNotNull(result);
            assertEquals("Groceries", result.title());
        }

        @Test
        void getCategoryById_ThrowsResourceNotFoundException() {
            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(userId, categoryId));
        }
    }

    @Nested
    class DeleteCategoryTests {

        @Test
        void deleteCategoryById_Success() {
            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.of(testCategory));

            categoryService.deleteCategoryById(userId, categoryId);

            assertFalse(testCategory.isActive());
        }

        @Test
        void deleteCategoryById_ThrowsResourceNotFoundException() {
            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(userId, categoryId));
        }
    }
}