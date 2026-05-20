package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.user.UserProfileRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;

import java.time.ZoneId;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    default ZoneId map(String timezone) {
        return timezone != null ? ZoneId.of(timezone) : ZoneId.of("UTC");
    }

    default String map(ZoneId timezone) {
        return timezone != null ? timezone.getId() : "UTC";
    }

    User toEntity(UserProfileRequest request);
    UserProfileResponse toResponse(User user);
    User partialUpdate(UserProfileRequest request, @MappingTarget User user);
}
