package com.cboard.marketplace.marketplace_backend.model.DtoMapping;

import com.cboard.marketplace.marketplace_backend.model.UserArchive;
import com.cboard.marketplace.marketplace_common.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper
{
    public UserArchive dtoToUserArchive(UserDto dto)
    {
        return new UserArchive(
                dto.getUserId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getUsername()
        );
    }

    public UserDto userArchiveToDto(UserArchive user)
    {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername()
        );

    }
}
