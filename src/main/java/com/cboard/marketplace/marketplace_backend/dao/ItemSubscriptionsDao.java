package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSubscriptionsDao extends JpaRepository<ItemSubscriptions, ItemSubscriptionsId>
{
    List<ItemSubscriptions> findByItemItemId(int itemId);

}
