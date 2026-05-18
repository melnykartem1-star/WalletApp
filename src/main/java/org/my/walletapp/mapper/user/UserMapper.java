package org.my.walletapp.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.user.UserProfileRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    User toEntity(UserProfileRequest request);
    UserProfileResponse toResponse(User user);
    User partialUpdate(UserProfileRequest request, @MappingTarget User user);
}
