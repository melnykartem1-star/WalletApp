package org.my.walletapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.my.walletapp.dto.user.UserProfilePatchRequest;
import org.my.walletapp.dto.user.UserProfileRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;

import java.time.ZoneId;
import java.util.Locale;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    default ZoneId mapZone(String timezone) {
        return timezone != null ? ZoneId.of(timezone) : null;
    }

    default String mapZone(ZoneId timezone) {
        return timezone != null ? timezone.getId() : null;
    }

    default Locale mapLocale(String locale) {
        return locale != null ? Locale.forLanguageTag(locale) : null;
    }

    default String mapLocale(Locale locale) {
        return locale != null ? locale.toLanguageTag() : null;
    }

    User toEntity(UserProfileRequest request);
    UserProfileResponse toResponse(User user);

    void partialUpdate(UserProfilePatchRequest request, @MappingTarget User user);
}
