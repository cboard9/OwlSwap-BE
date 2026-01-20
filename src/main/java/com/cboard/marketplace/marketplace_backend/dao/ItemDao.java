package com.cboard.marketplace.marketplace_backend.dao;

import com.cboard.marketplace.marketplace_backend.model.Item;

import com.cboard.marketplace.marketplace_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Item> findByUserUserIdAndAvailableTrue(Integer userId, Pageable pageable);
    Page<Item> findByAvailableTrueAndUserUserIdNot(Integer userId, Pageable pageable);

   // Page<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Integer userId, String nameKeyword, String descriptionKeyword, Pageable pageable);
    Page<Item> findByUserUserIdNotAndNameContainingIgnoreCaseAndAvailableTrueOrUserUserIdNotAndDescriptionContainingIgnoreCaseAndAvailableTrue(Integer userId, String nameKeyword, Integer userId_, String descriptionKeyword, Pageable pageable);

    Page<Item> findByCategoryNameAndAvailableTrueAndUserUserIdNot(String category, Integer userId, Pageable pageable);

    Page<Item> findByUserUserIdNotAndAvailableTrueAndNameContainingIgnoreCaseOrUserUserIdNotAndAvailableTrueAndDescriptionContainingIgnoreCaseOrUserUserIdNotAndAvailableTrueAndCategoryName
            (Integer userId, String nKey, Integer userId_, String dKey, Integer userId__, String cat, Pageable pageable);

    //search a keyword WITHIN a category
    Page<Item> findByUserUserIdNotAndAvailableTrueAndNameContainingIgnoreCaseAndCategoryNameOrUserUserIdNotAndAvailableTrueAndDescriptionContainingIgnoreCaseAndCategoryName
            (Integer userId, String nKey, String cat, Integer userId_, String dKey, String cat_, Pageable pageable);


    @Procedure(procedureName = "soft_delete_item")
    public void softDeleteItem(int itemId);

}
