package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.category.CategoryRequest;
import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.entity.Category;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    Category toEntity(CategoryRequest request);
    CategoryResponse toResponse(Category category);
    Category partialUpdate(CategoryRequest request, @MappingTarget Category category);
}
