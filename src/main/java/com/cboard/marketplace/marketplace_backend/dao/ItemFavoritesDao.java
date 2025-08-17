package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemFavoritesDao extends JpaRepository<ItemFavorite, ItemFavoriteId>
{
    List<ItemFavorite> findByItemItemId(int itemId);
    Page<ItemFavorite> findByUserUserId(int userId, Pageable pageable);

}
