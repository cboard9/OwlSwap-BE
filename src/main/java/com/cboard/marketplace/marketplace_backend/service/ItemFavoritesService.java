package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.ItemDao;
import com.cboard.marketplace.marketplace_backend.dao.ItemFavoritesDao;
import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.Dto.ItemDto;
import com.cboard.marketplace.marketplace_backend.model.DtoMapping.fromDto.DtoToItemFactory;
import com.cboard.marketplace.marketplace_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.marketplace.marketplace_backend.model.Item;
import com.cboard.marketplace.marketplace_backend.model.ItemFavorite;
import com.cboard.marketplace.marketplace_backend.model.ItemFavoriteId;
import com.cboard.marketplace.marketplace_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemFavoritesService
{
    @Autowired
    ItemFavoritesDao dao;
    @Autowired
    ItemDao itemDao;
    @Autowired
    UserDao userDao;
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
    public ItemFavoritesService(ItemFavoritesDao dao, ItemToDtoFactory toDtoFactory, DtoToItemFactory fromDtoFactory) {
        this.dao = dao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }

    public ResponseEntity<List<ItemFavorite>> getItemFavoritesByItem(int itemId)
    {
        try
        {
            List<ItemFavorite> itemSubscriptions = dao.findByItemItemId(itemId);
            return new ResponseEntity<>(itemSubscriptions, HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Page<ItemDto>> getItemFavoritesByUser(int userId, Pageable pageable)
    {

        Page<ItemFavorite> favorites = dao.findByUserUserId(userId, pageable);
        Page<Item> items = favorites.map(ItemFavorite::getItem);

        Page<ItemDto> dtoPage = items
                .map(item -> {
                            try {
                                return toDtoFactory.toDto(item);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                throw new RuntimeException("Error converting item to DTO: " + item, e);
                            }
                        }
                );

        return new ResponseEntity<>(dtoPage, HttpStatus.OK);


    }

    public ResponseEntity<String> addFavorite(int userId, int itemId) {
        User user = userDao.findById(userId);
        if(user == null)
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        Item item = itemDao.findByItemId(itemId);
        if(item == null)
            return new ResponseEntity<>("Item not found", HttpStatus.BAD_REQUEST);

        ItemFavoriteId itemFavoriteId = new ItemFavoriteId(itemId, userId);
        ItemFavorite itemFavorite = new ItemFavorite(itemFavoriteId, item, user);

        dao.save(itemFavorite);

        return new ResponseEntity<>("Item favorited", HttpStatus.OK);

    }

    public ResponseEntity<String> deleteFavorite(int userId, int itemId) {

        ItemFavoriteId itemFavoriteId = new ItemFavoriteId(itemId, userId);
        ItemFavorite itemFavorite = dao.findItemFavoriteByItemFavoriteId(itemFavoriteId);
        if(itemFavorite == null)
            return new ResponseEntity<>("Favorited item not found", HttpStatus.BAD_REQUEST);

        dao.delete(itemFavorite);

        return new ResponseEntity<>("Favorited item removed", HttpStatus.OK);
    }
}
