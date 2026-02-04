package com.cboard.owlswap.owlswap_backend.service;
import com.cboard.owlswap.owlswap_backend.dao.UserSubscriptionsDao;
import com.cboard.owlswap.owlswap_backend.model.UserSubscriptions;
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

    public ResponseEntity<List<UserSubscriptions>> getAllUserSubscriptions(int userId)
    {
        try
        {
            List<UserSubscriptions> userSubscriptions = dao.findByUserUserId(userId);
            return new ResponseEntity<>(userSubscriptions, HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

}
