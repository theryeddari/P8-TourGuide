package com.openclassrooms.tourguide.model.user;

import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a user in the tour guide system.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private static final Logger logger = LogManager.getLogger(User.class);

    private final UUID userId;
    private final String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private CopyOnWriteArrayList<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
    private List<UserReward> userRewards = new ArrayList<>();
    private UserPreferences userPreferences = new UserPreferences();
    private List<Provider> tripDeals = new ArrayList<>();

    /**
     * Constructs a User with the given ID, name, phone number, and email address.
     *
     * @param userId       The unique identifier for the user.
     * @param userName     The name of the user.
     * @param phoneNumber  The phone number of the user.
     * @param emailAddress The email address of the user.
     */


    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    /**
     * Adds a visited location to the user's list of visited locations.
     *
     * @param visitedLocation The visited location to add.
     */
    public void addToVisitedLocations(VisitedLocation visitedLocation) {
        visitedLocations.add(visitedLocation);
        logger.info("Visited location added: {}", visitedLocation);
    }

    /**
     * Clears all visited locations from the user's list.
     */
    public void clearVisitedLocations() {
        visitedLocations.clear();
        logger.info("All visited locations cleared.");
    }

    /**
     * Adds a user reward if there isn't already a reward for the same attraction.
     *
     * @param userReward The user reward to add.
     */
    public void addUserReward(UserReward userReward) {
        // Filter all userRewards in the list<UserReward> that do not have an attractionName equal to the attractionName of the userReward passed as a parameter
        // to ensure that there are none. If this is the case the condition is respected and the userReward passed in parameter is added to list userRewards, otherwise nothing
        if (userRewards.stream().noneMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName))) {
            userRewards.add(userReward);
            logger.info("User reward added: {}", userReward);
        } else {
            logger.debug("User reward not added as an existing reward for the same attraction is already present.");
        }
    }

    /**
     * Retrieves the most recently visited location.
     *
     * @return The last visited location.
     */
    public VisitedLocation getLastVisitedLocation() {
        VisitedLocation lastLocation = visitedLocations.getLast();
        logger.debug("Retrieved last visited location: {}", lastLocation);
        return lastLocation;
    }
}

