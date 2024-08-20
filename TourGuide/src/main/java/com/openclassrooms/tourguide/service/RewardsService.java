package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Setter;
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
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;

    // The CachedThreadPool dynamically adjusts the number of threads based on demand, which is ideal for I/O-bound tasks.
    // This pool starts with zero threads and can grow as needed, creating new threads for each new task if no idle threads are available.
    // The Semaphore with 100 permits controls the maximum number of concurrent tasks, preventing the system from being overwhelmed.
    // This configuration balances responsiveness and resource utilization, allowing the pool to scale while ensuring we don't exceed the desired concurrency limit.
    Executor executor = Executors.newCachedThreadPool();
    Semaphore semaphore = new Semaphore(100);

    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    public void calculateRewards(User user) {
        try {
            semaphore.acquire();
            // Retrieve the user's visited locations
            List<VisitedLocation> userLocations = user.getVisitedLocations();
            CompletableFuture.supplyAsync(gpsUtil::getAttractions, executor).thenAccept(attractions -> {


                //for each VisitedLocation of List<VisitedLocation> named userLocations, I filter all attraction of List<Attraction> named attractions
                // and keep only attractions that user not visited thanks to the past proposal of the application (rewards)
                // (so not present in List<userRewards> of User given by user.getUserReward)
                //Finally i creat a list of UserReward, each one build with one visitedLocation and this attraction.
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
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            semaphore.release();
        }

    }
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return !(getDistance(attraction, location) > attractionProximityRange);
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    private int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

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
