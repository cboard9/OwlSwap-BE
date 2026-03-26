package com.cboard.owlswap.owlswap_backend.model;

import com.cboard.owlswap.owlswap_backend.security.RefreshToken;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Transient
    private Double averageRating;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
    @Column(name = "stripe_account_id")
    private String stripeAccountId;

    @Column(name = "stripe_onboarding_complete")
    private boolean stripeOnboardingComplete;

    @Column(name = "stripe_charges_enabled")
    private boolean stripeChargesEnabled;

    @Column(name = "stripe_payouts_enabled")
    private boolean stripePayoutsEnabled;

    public User() {
    }

    public User(Integer userId, String firstName, String lastName, String email, String username, String password, Double averageRating) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.averageRating = averageRating;
    }

    public User(Integer userId, String firstName, String lastName, String email, String username, String password, Double averageRating,
                String stripeAccountId,
                boolean stripeOnboardingComplete,
                boolean stripeChargesEnabled,
                boolean stripePayoutsEnabled) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.averageRating = averageRating;
        this.stripeAccountId = stripeAccountId;
        this.stripeOnboardingComplete = stripeOnboardingComplete;
        this.stripeChargesEnabled = stripeChargesEnabled;
        this.stripePayoutsEnabled = stripePayoutsEnabled;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    public boolean isStripeOnboardingComplete() {
        return stripeOnboardingComplete;
    }

    public void setStripeOnboardingComplete(boolean stripeOnboardingComplete) {
        this.stripeOnboardingComplete = stripeOnboardingComplete;
    }

    public boolean isStripeChargesEnabled() {
        return stripeChargesEnabled;
    }

    public void setStripeChargesEnabled(boolean stripeChargesEnabled) {
        this.stripeChargesEnabled = stripeChargesEnabled;
    }

    public boolean isStripePayoutsEnabled() {
        return stripePayoutsEnabled;
    }

    public void setStripePayoutsEnabled(boolean stripePayoutsEnabled) {
        this.stripePayoutsEnabled = stripePayoutsEnabled;
    }
}