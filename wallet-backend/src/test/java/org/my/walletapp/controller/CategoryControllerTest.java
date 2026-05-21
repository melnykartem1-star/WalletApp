package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.CategoryType;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User testUser;
    private CategoryResponse testResponse;
    private final Long userId = 1L;
    private final Long categoryId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testResponse = new CategoryResponse(categoryId, "Groceries", "Food", CategoryType.EXPENSE, true, "#FFFFFF", "icon");
    }

    @Test
    void getAllCategories_ShouldReturn200AndPage() throws Exception {
        when(categoryService.getAllCategories(any(Pageable.class), eq(userId)))
                .thenReturn(new PageImpl<>(List.of(testResponse)));

        mockMvc.perform(get("/api/v1/categories")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Groceries"));
    }

    @Test
    void getCategoryById_ShouldReturn200() throws Exception {
        when(categoryService.getCategoryById(userId, categoryId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Groceries"));
    }

    @Test
    void getCategoryById_ShouldReturn404() throws Exception {
        when(categoryService.getCategoryById(userId, 999L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/categories/{id}", 999L)
                        .with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_ShouldReturn201() throws Exception {
        CategoryRequest request = new CategoryRequest("New Category", "Desc", CategoryType.INCOME, null, null);

        when(categoryService.createCategory(eq(userId), any(CategoryRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/categories")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Groceries"));
    }

    @Test
    void updateCategoryById_ShouldReturn200() throws Exception {
        CategoryRequest request = new CategoryRequest("Updated", "Desc", CategoryType.EXPENSE, null, null);

        when(categoryService.updateCategoryById(eq(userId), eq(categoryId), any(CategoryRequest.class))).thenReturn(testResponse);

        mockMvc.perform(patch("/api/v1/categories/{id}", categoryId)
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategoryById_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
