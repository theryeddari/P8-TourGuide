package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.InfoAboutNearbyAttractionDTO;
import com.openclassrooms.tourguide.dto.NearbyAttractionResponse;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import rewardCentral.RewardCentral;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private final Logger logger = LogManager.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	private final RewardCentral rewardCentral = new RewardCentral();
	public final Tracker tracker;
	boolean testMode = true;

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

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		// we retrieve the location in the user details if it exists otherwise we just launch the tracker to retrieve it
        return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation() : trackUserLocation(user);
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	/**
	 * Retrieves the five closest tourist attractions to the user's current location.
	 * This method calculates the distance between the user's location and each attraction,
	 * sorts the attractions by proximity, and returns the closest five.
	 * The resulting data includes the attraction's name, coordinates, distance from the user,
	 * and reward points available for visiting each attraction.
	 *
	 * @param visitedLocation The user's current visited location, containing their coordinates.
	 * @param user The user for whom the nearby attractions are being retrieved.
	 * @return A NearbyAttractionResponse containing the five closest attractions and relevant information.
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
					Double distance = rewardsService.getDistance(locationUser,attraction);

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

	/**********************************************************************************
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
        logger.debug("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())));
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
