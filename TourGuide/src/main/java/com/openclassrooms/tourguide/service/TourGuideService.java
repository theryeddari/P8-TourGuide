package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.InfoAboutNearbyAttractionDTO;
import com.openclassrooms.tourguide.dto.NearbyAttractionResponse;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.service.tracker.Tracker;
import com.openclassrooms.tourguide.utils.helper.InternalTestHelper;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static exception.RewardsServiceException.CalulateRewardsException;
import static exception.TourGuideServiceException.TrackUserLocationException;

/**
 * The {@code TourGuideService} class handles operations related to the Tour Guide system user,
 * including retrieving the user's location, managing rewards,
 * and providing travel deals.
 */
@Service
public class TourGuideService {
    /**********************************************************************************
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/

    private static final String tripPricerApiKey = "test-server-api-key";
    public final Tracker tracker;
    private final Logger logger = LogManager.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();
    private final RewardCentral rewardCentral = new RewardCentral();
    // Database connection will be used for external users, but for testing purposes
    // internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();
    boolean testMode = true;
    // ForkJoinPool have 7 thread by default, we use I/O bound, we can up easily to 40 (trade-off between resource and performance for the requested task)
    Executor executor = Executors.newFixedThreadPool(40);

    /**
     * Constructor for {@code TourGuideService}.
     * Initializes GPS and reward services, as well as internal users if test mode is enabled.
     *
     * @param gpsUtil        The GPS service used to obtain location information.
     * @param rewardsService The rewards service used to calculate reward points.
     */
    public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        Locale.setDefault(Locale.US);

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    /**
     * Retrieves the user's rewards.
     *
     * @param user The user whose rewards are retrieved.
     * @return A list of the user's rewards.
     */
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    /**
     * Gets the user's location. If the user has already visited locations, returns the last visited location,
     * otherwise starts tracking the user's location.
     *
     * @param user The user whose location is retrieved.
     * @return The user's visited location.
     * @throws TrackUserLocationException If an error occurs while tracking the location.
     */
    public VisitedLocation getUserLocation(User user) throws TrackUserLocationException {
        // we retrieve the location in the user details if it exists otherwise we just launch the tracker to retrieve it
        return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation() : trackUserLocation(user).join();
    }

    /**
     * Retrieves a user based on their username.
     *
     * @param userName The username of the user to retrieve.
     * @return The user corresponding to the username.
     */
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    /**
     * Retrieves all internal users.
     *
     * @return A list of all internal users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    /**
     * Adds a user to the internal user list.
     *
     * @param user The user to add.
     */
    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    /**
     * Retrieves travel deals for a user based on accumulated reward points.
     *
     * @param user The user for whom travel deals are retrieved.
     * @return A list of providers offering travel deals.
     */
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    /**
     * Tracks the user's location asynchronously.
     *
     * @param user The user whose location is tracked.
     * @return A {@code CompletableFuture} containing the user's visited location.
     * @throws TrackUserLocationException If an error occurs while tracking the location.
     */
    public CompletableFuture<VisitedLocation> trackUserLocation(User user) throws TrackUserLocationException {
        try {
            // Use CompletableFuture.supplyAsync to obtain the user's location asynchronously
            return CompletableFuture.supplyAsync(() -> gpsUtil.getUserLocation(user.getUserId()), executor)
                    .thenApply(location -> {
                        // Add location to the user's list of visited locations
                        user.addToVisitedLocations(location);
                        // Calculate the rewards for the user synchronously
                        try {
                            rewardsService.calculateRewards(user);
                        } catch (CalulateRewardsException e) {
                            logger.error("Error calculating rewards", e);
                            throw new RuntimeException(e);
                        }
                        // Return the location after rewards have been calculated
                        return location;
                    });
        } catch (Exception e) {
            logger.error("Error tracking user location", e);
            throw new TrackUserLocationException(e);
        }
    }

    /**
     * Retrieves the five closest tourist attractions to the user's current location.
     * This method calculates the distance between the user's location and each attraction,
     * sorts the attractions by proximity, and returns the five closest.
     *
     * @param visitedLocation The user's current location.
     * @param user            The user for whom nearby attractions are retrieved.
     * @return A response containing the five closest attractions and relevant information.
     */
    public NearbyAttractionResponse getNearByAttractions(VisitedLocation visitedLocation, User user) {
        // Log the start of the method execution
        logger.info("Start fetching nearby attractions for user: {}", user.getUserName());

        // Get the user's current location from the VisitedLocation object
        Location locationUser = visitedLocation.location;

        // Create a list of the five nearest attractions to the user
        List<InfoAboutNearbyAttractionDTO> infoAboutNearbyAttractionDTOS = gpsUtil.getAttractions().stream()
                .map(attraction -> {
                    // Calculate the distance between the user's location and the attraction
                    Double distance = rewardsService.getDistance(locationUser, attraction);

                    // Log the calculated distance for debugging purposes
                    logger.debug("Calculated distance for attraction {}: {} miles", attraction.attractionName, distance);

                    // Return a map entry containing the attraction and its distance from the user
                    return new AbstractMap.SimpleEntry<>(attraction, distance);
                })
                // Sort the map entries by the distance (in ascending order)
                .sorted(Map.Entry.comparingByValue())
                // Limit the result to the top 5 closest attractions
                .limit(5)
                // Map the sorted entries to InfoAboutNearbyAttractionDTO objects
                .map(attractionSorted -> new InfoAboutNearbyAttractionDTO(
                        attractionSorted.getKey().attractionName, // Name of the attraction
                        List.of(
                                Map.of("X", attractionSorted.getKey().longitude), // Longitude of the attraction
                                Map.of("Y", attractionSorted.getKey().latitude)   // Latitude of the attraction
                        ),
                        List.of(
                                Map.of("X", locationUser.longitude), // Longitude of the user's location
                                Map.of("Y", locationUser.latitude)   // Latitude of the user's location
                        ),
                        attractionSorted.getValue(), // Distance from the user
                        rewardCentral.getAttractionRewardPoints(attractionSorted.getKey().attractionId, user.getUserId()) // Reward points
                ))
                .toList();

        // Log the completion of the nearby attraction retrieval
        logger.info("Successfully fetched nearby attractions for user: {}", user.getUserName());

        // Return the response containing the list of nearby attractions
        return new NearbyAttractionResponse(infoAboutNearbyAttractionDTOS);
    }

    /**
     * Registers a shutdown hook to ensure that the tracker stops tracking
     * when the Java Virtual Machine (JVM) is shutting down.
     */
    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }

    /**
     * Initializes internal test users. The number of users initialized is determined
     * by the {@code InternalTestHelper} class.
     */
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.info("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
    }

    /**
     * Generates a history of visited locations for a user.
     *
     * @param user The user for whom the location history is generated.
     */
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
    }

    /**
     * Generates a random time within the last 30 days.
     *
     * @return A random {@code Date} within the last 30 days.
     */
    private Date getRandomTime() {
        LocalDateTime randomDate = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(randomDate.toInstant(ZoneOffset.UTC));
    }

    /**
     * Generates a random latitude value.
     *
     * @return A random latitude value.
     */
    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    /**
     * Generates a random longitude value.
     *
     * @return A random longitude value.
     */
    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

}
