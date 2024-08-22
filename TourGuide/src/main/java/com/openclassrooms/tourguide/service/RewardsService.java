package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.exception.RewardsServiceException.CalulateRewardsException;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Service
@Setter
public class RewardsService {
    private static final Logger logger = LogManager.getLogger(RewardsService.class);
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    // The CachedThreadPool dynamically adjusts the number of threads based on demand, which is ideal for I/O-bound tasks.
    // This pool starts with zero threads and can grow as needed, creating new threads for each new task if no idle threads are available.
    // The Semaphore with 100 permits controls the maximum number of concurrent tasks, preventing the system from being overwhelmed.
    // This configuration balances responsiveness and resource utilization, allowing the pool to scale while ensuring we don't exceed the desired concurrency limit.
    private final Executor executor = Executors.newCachedThreadPool();
    private final Semaphore semaphore = new Semaphore(100);
    // Default proximity buffer in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    /**
     * Constructor for RewardsService.
     *
     * @param gpsUtil       An instance of {@link GpsUtil} for retrieving GPS data.
     * @param rewardCentral An instance of {@link RewardCentral} for calculating reward points.
     */
    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    /**
     * Calculates rewards for the given user based on their visited locations and nearby attractions.
     *
     * @param user The user for whom rewards are to be calculated.
     * @throws CalulateRewardsException if an error occurs during the reward calculation process.
     */
    public void calculateRewards(User user) throws CalulateRewardsException {
        try {
            semaphore.acquire();
            // Retrieve the user's visited locations
            List<VisitedLocation> userLocations = user.getVisitedLocations();
            CompletableFuture.supplyAsync(gpsUtil::getAttractions, executor).thenAccept(attractions -> {

                // For each VisitedLocation of List<VisitedLocation> named userLocations, filter all attractions from List<Attraction> named attractions
                // and keep only attractions that the user has not visited (i.e., not present in List<userRewards> of User).
                // Create a list of UserReward, each one built with one visitedLocation and this attraction.
                userLocations.stream().flatMap(visitedLocation ->
                        attractions.stream()
                                .filter(attraction ->
                                        user.getUserRewards()
                                                .stream()
                                                .noneMatch(reward -> reward.getAttraction().attractionName.equals(attraction.attractionName)))
                                //Now I check if the attractions not visited by the user are close to the list of places where he is located into userLocations List<VisitedLocation>
                                // thank lambda stream for each userLocation and attraction with nearAttraction boolean method.
                                .filter(attraction -> nearAttraction(visitedLocation, attraction))
                                //for each nearby attraction I create a UserReward, (suggestion of the place to the user which includes
                                // the place visited, attraction to visit and "the id of attraction and user into RewardPoints")
                                // and put list<UserReward> named updateRewards I add the suggestions of the places to the user detail
                                .map(attraction -> new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)))).forEach(user::addUserReward);
            }).join();
        } catch (Exception e) {
            logger.error("Error occurred while calculating rewards", e);
            throw new CalulateRewardsException(e);
        } finally {
            semaphore.release();
        }
    }

    /**
     * Checks if the given attraction is within the proximity range of the given location.
     *
     * @param attraction The attraction to check.
     * @param location   The location to check against.
     * @return {@code true} if the attraction is within the proximity range; {@code false} otherwise.
     */
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) <= attractionProximityRange;
    }

    /**
     * Checks if the given attraction is near the visited location based on the proximity buffer.
     *
     * @param visitedLocation The visited location to check.
     * @param attraction      The attraction to check.
     * @return {@code true} if the attraction is near the visited location; {@code false} otherwise.
     */
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
    }

    /**
     * Retrieves the reward points for the given attraction and user.
     *
     * @param attraction The attraction for which reward points are to be retrieved.
     * @param user       The user for whom the reward points are to be retrieved.
     * @return The reward points for the given attraction and user.
     */
    private int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    /**
     * Calculates the distance between two locations in miles.
     *
     * @param loc1 The first location.
     * @param loc2 The second location.
     * @return The distance between the two locations in miles.
     */
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}
