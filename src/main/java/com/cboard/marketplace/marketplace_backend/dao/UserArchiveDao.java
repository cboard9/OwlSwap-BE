package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Item;

import com.cboard.marketplace.marketplace_backend.model.User;
import com.cboard.marketplace.marketplace_backend.model.UserArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserArchiveDao extends JpaRepository<UserArchive, Integer> {
    Optional<UserArchive> findByUsername(String username);
    UserArchive findById(int userId);
}
