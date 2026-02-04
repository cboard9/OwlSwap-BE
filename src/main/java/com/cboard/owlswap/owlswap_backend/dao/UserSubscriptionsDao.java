package com.cboard.owlswap.owlswap_backend.dao;

import com.cboard.owlswap.owlswap_backend.model.UserSubscriptions;
import com.cboard.owlswap.owlswap_backend.model.UserSubscriptionsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSubscriptionsDao extends JpaRepository<UserSubscriptions, UserSubscriptionsId>
{
    List<UserSubscriptions> findByUserUserId(int userId);
}
