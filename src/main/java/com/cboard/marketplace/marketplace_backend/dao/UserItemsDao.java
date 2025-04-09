package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Item;

import com.cboard.marketplace.marketplace_backend.model.User;
import com.cboard.marketplace.marketplace_backend.model.UserItems;
import com.cboard.marketplace.marketplace_backend.model.UserItemsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemsDao extends JpaRepository<UserItems, UserItemsId>
{
    List<UserItems> findByUserUserId(int userId);

}
