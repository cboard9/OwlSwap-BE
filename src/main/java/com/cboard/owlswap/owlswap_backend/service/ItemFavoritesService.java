package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.dao.ItemDao;
import com.cboard.owlswap.owlswap_backend.dao.ItemFavoritesDao;
import com.cboard.owlswap.owlswap_backend.dao.UserDao;
import com.cboard.owlswap.owlswap_backend.exception.DtoMappingException;
import com.cboard.owlswap.owlswap_backend.exception.NotAvailableException;
import com.cboard.owlswap.owlswap_backend.exception.NotFoundException;
import com.cboard.owlswap.owlswap_backend.model.Dto.ItemDto;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.fromDto.DtoToItemFactory;
import com.cboard.owlswap.owlswap_backend.model.DtoMapping.toDto.ItemToDtoFactory;
import com.cboard.owlswap.owlswap_backend.model.Item;
import com.cboard.owlswap.owlswap_backend.model.ItemFavorite;
import com.cboard.owlswap.owlswap_backend.model.ItemFavoriteId;
import com.cboard.owlswap.owlswap_backend.model.User;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    CurrentUser currentUser;
    private final ItemToDtoFactory toDtoFactory;
    private final DtoToItemFactory fromDtoFactory;

    //this automatically adds all the different dto mappers through injection
    public ItemFavoritesService(ItemFavoritesDao dao, ItemToDtoFactory toDtoFactory, DtoToItemFactory fromDtoFactory) {
        this.dao = dao;
        this.toDtoFactory = toDtoFactory;
        this.fromDtoFactory = fromDtoFactory;
    }

    public List<ItemFavorite> getItemFavoritesByItem(int itemId)
    {
        Item item = itemDao.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found. itemId=" + itemId);
        }

        //favorites for an item only viewable by that item's user
        if (item.getUser().getUserId() != currentUser.userId()) {
            throw new AccessDeniedException("You cannot view favorites for this item.");
        }

        return dao.findByItemItemId(itemId);
    }

    public Page<ItemDto> getMyItemFavorites(Pageable pageable)
    {
        int userId = currentUser.userId();

        Page<ItemFavorite> favorites = dao.findByUserUserId(userId, pageable);
        Page<Item> items = favorites.map(ItemFavorite::getItem);

        return items
                .map(item -> {
                            try {
                                return toDtoFactory.toDto(item);
                            } catch (IllegalArgumentException e) {
                                throw new DtoMappingException("Error converting item to DTO. itemId=" + item.getItemId(), e);
                            }
                        }
                );
    }

    @Transactional
    public void addFavorite(int itemId)
    {
        int userId = currentUser.userId();
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. userId=" + userId));

        Item item = itemDao.findByItemId(itemId);
        if(item == null)
            throw new NotFoundException("Item not found. itemId=" + itemId);

        if (!item.isAvailable()) {
            throw new NotFoundException("Item not available.");
        }

        ItemFavoriteId itemFavoriteId = new ItemFavoriteId(itemId, userId);
        ItemFavoriteId id = new ItemFavoriteId(itemId, userId);

        if (dao.existsById(id)) {
            //consider changing this to a specific CONFLICT exception
            throw new NotAvailableException("Item is already favorited.");
        }

        ItemFavorite itemFavorite = new ItemFavorite(itemFavoriteId, item, user);

        dao.save(itemFavorite);
    }

    public void removeFavorite(int itemId)
    {
        int userId = currentUser.userId();
        ItemFavoriteId id = new ItemFavoriteId(itemId, userId);

        ItemFavorite fav = dao.findItemFavoriteByItemFavoriteId(id);
        if (fav != null) {
            dao.delete(fav);
        }
    }

    public Boolean getIsFavorite(int itemId)
    {
        int userId = currentUser.userId();

        return dao.existsByItemFavoriteId(new ItemFavoriteId(itemId, userId));
    }
}
