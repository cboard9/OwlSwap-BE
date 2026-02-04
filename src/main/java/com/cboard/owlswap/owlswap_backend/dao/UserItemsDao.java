package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.UserItems;
import com.cboard.owlswap.owlswap_backend.model.UserItemsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemsDao extends JpaRepository<UserItems, UserItemsId>
{
    List<UserItems> findByUserUserId(int userId);

}
