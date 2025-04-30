package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Item;

import com.cboard.marketplace.marketplace_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemDao extends JpaRepository<Item, Integer>
{
    Item findByItemId(int itemId);

    List<Item> findAllByUser_UserId(int userId);


    @Procedure(procedureName = "soft_delete_item")
    public void softDeleteItem(int itemId);

}
