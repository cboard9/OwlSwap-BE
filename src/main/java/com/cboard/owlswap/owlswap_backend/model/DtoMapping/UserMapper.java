package com.cboard.owlswap.owlswap_backend.model.DtoMapping;

import com.cboard.owlswap.owlswap_backend.model.Dto.UserDto;
import com.cboard.owlswap.owlswap_backend.model.UserArchive;
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
                dto.getUsername(),
                dto.getAverageRating()
        );
    }

    public UserDto userArchiveToDto(UserArchive user)
    {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAverageRating()
        );

    }
}
