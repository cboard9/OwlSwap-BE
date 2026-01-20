package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.Dto.UserDto;
import com.cboard.marketplace.marketplace_backend.model.User;
import com.cboard.marketplace.marketplace_backend.model.UserArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService
{
    @Autowired
    UserDao dao;
    @Autowired
    RatingService ratingService;




    public ResponseEntity<List<User>> getAllUsers()
    {
        try
        {
            return new ResponseEntity<>(dao.findAll(), HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public User findById(int userId)
    {
        return dao.findById(userId);
    }

    public ResponseEntity<String> deleteUser(int userId)
    {
        try
        {
            User user = dao.findById(userId);
            dao.delete(user);
            return new ResponseEntity<>("Account deleted", HttpStatus.OK);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting user", HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<UserDto> getProfile() {
        //retrieve authenticated username directly
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        User user = dao.findByUsername(username).orElseThrow();

        Double avgRating = ratingService.calculateAverageRating(user.getUserId());
        user.setAverageRating(avgRating);

        // convert user to userDto
        UserDto userDto = new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAverageRating()
        );

        return ResponseEntity.ok(userDto);
    }
}
