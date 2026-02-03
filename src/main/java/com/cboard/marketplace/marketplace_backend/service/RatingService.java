package com.cboard.marketplace.marketplace_backend.service;

import com.cboard.marketplace.marketplace_backend.dao.RatingDao;
import com.cboard.marketplace.marketplace_backend.dao.UserDao;
import com.cboard.marketplace.marketplace_backend.model.Rating;
import com.cboard.marketplace.marketplace_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingDao ratingDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    public ResponseEntity<String> addRating(int userId, double score) {
        if (score < 0.0 || score > 5.0) {
            return ResponseEntity.badRequest().body("Rating must be between 0.0 and 5.0");
        }

        List<User> users = userDao.findAll();
        User user = null;
        for (User u : users) {
            if (u.getUserId() == userId) {
                user = u;
                break;
            }
        }

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Rating rating = new Rating(user, score);
        ratingDao.save(rating);
        Double newAvg = calculateAverageRating(user.getUserId());
        user.setAverageRating(newAvg);
        userDao.save(user);
        return ResponseEntity.ok("Rating submitted successfully");
    }

    public List<Rating> getRatingsForUser(int userId) {
        return ratingDao.findByUserUserId(userId);
    }

    public Double calculateAverageRating(int userId) {
        List<Rating> ratings = ratingDao.findByUserUserId(userId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        double sum = ratings.stream().mapToDouble(Rating::getScore).sum();
        return sum / ratings.size();
    }
}