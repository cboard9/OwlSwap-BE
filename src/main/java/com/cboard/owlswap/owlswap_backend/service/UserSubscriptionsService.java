package com.cboard.owlswap.owlswap_backend.service;
import com.cboard.owlswap.owlswap_backend.dao.UserSubscriptionsDao;
import com.cboard.owlswap.owlswap_backend.model.UserSubscriptions;
import com.cboard.owlswap.owlswap_backend.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSubscriptionsService
{
    @Autowired
    UserSubscriptionsDao dao;
    @Autowired
    CurrentUser currentUser;

    public List<UserSubscriptions> getAllUserSubscriptions()
    {

        int userId = currentUser.userId();
        List<UserSubscriptions> userSubscriptions = dao.findByUserUserId(userId);
        return userSubscriptions;
    }

}
