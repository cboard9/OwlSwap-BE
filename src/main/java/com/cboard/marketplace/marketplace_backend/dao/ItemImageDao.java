package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageDao extends JpaRepository<ItemImage, Integer> {
    List<ItemImage> findAllByItemItemId(int itemId);
}
