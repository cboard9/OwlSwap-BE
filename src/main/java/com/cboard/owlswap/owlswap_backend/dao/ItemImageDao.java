package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageDao extends JpaRepository<ItemImage, Integer> {
    List<ItemImage> findAllByItemItemId(int itemId);
}
