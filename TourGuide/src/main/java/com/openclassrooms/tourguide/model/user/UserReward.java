package com.openclassrooms.tourguide.model.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@code UserReward} class represents a reward given to a user for visiting an attraction.
 * It contains information about the visited location, the associated attraction, and the reward points.
 */

@Setter
@Getter
@AllArgsConstructor
public class UserReward {


    private static final Logger logger = LogManager.getLogger(UserReward.class);

    /**
     * The location visited by the user.
     */
    public final VisitedLocation visitedLocation;

    /**
     * The attraction associated with the reward.
     */
    public final Attraction attraction;

    /**
     * The amount of reward points accumulated.
     */
    private int rewardPoints;

    /**
     * Constructs a {@code UserReward} instance with the specified visited location and attraction.
     *
     * @param visitedLocation The location visited by the user.
     * @param attraction      The attraction associated with the reward.
     */
    public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        logger.debug("Creating UserReward with visitedLocation: {} and attraction: {}", visitedLocation, attraction);
    }

}

