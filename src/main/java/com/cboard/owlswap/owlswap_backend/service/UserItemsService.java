package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.UserItemsDao;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.UserItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserItemsService
{
    @Autowired
    UserItemsDao dao;
    @Autowired
    ItemToDtoFactory toDtoFactory;


    public List<ItemDto> getAllUserItems(int userId)
    {

            //only return the items, no user info?
        List<UserItems> userItems = dao.findByUserUserId(userId);
        return userItems.stream()
                .map(UserItems::getItem)
                .map(item -> {
                            try
                            {
                                return toDtoFactory.toDto(item);
                            }
                            catch(Exception e)
                            {
                                throw new DtoMappingException("Error converting item to DTO: " + item, e);
                            }
                        }
                )
                .toList();
    }
}
