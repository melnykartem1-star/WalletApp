package org.my.walletapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.service.category.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable, user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(user.getId(), categoryId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(user.getId(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategoryById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategoryById(user.getId(), categoryId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(
            @AuthenticationPrincipal User user,
            @PathVariable(name = "id") Long categoryId) {
        categoryService.deleteCategoryById(user.getId(), categoryId);
        return ResponseEntity.noContent().build();
    }

}
