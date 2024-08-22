package com.openclassrooms.tourguide.controller;

import com.openclassrooms.tourguide.dto.NearbyAttractionResponse;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;
import com.openclassrooms.tourguide.service.TourGuideService;
import gpsUtil.location.VisitedLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;

import static exception.TourGuideServiceException.TrackUserLocationException;


/**
 * The {@code TourGuideController} class handles HTTP requests related to tour guide services.
 * It provides endpoints for retrieving user location, nearby attractions, rewards, and trip deals.
 */
@RestController
public class TourGuideController {

    private static final Logger logger = LogManager.getLogger(TourGuideController.class);

    @Autowired
    private TourGuideService tourGuideService;

    /**
     * Default endpoint to check the service status.
     *
     * @return A greeting message from the TourGuide service.
     */
    @RequestMapping("/")
    public String index() {
        logger.info("Index endpoint accessed.");
        return "Greetings from TourGuide!";
    }

    /**
     * Endpoint to retrieve the location of a user.
     *
     * @param userName The username of the user whose location is to be retrieved.
     * @return The {@code VisitedLocation} of the specified user.
     * @throws TrackUserLocationException if there is an issue tracking the user's location.
     */
    @GetMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) throws TrackUserLocationException {
        logger.debug("Getting location for user: {}", userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        logger.info("Retrieved location for user: {}", userName);
        return visitedLocation;
    }

    /**
     * Endpoint to retrieve nearby attractions for a user.
     *
     * @param userName The username of the user for whom nearby attractions are to be retrieved.
     * @return The {@code NearbyAttractionResponse} containing nearby attractions for the user.
     * @throws TrackUserLocationException if there is an issue tracking the user's location.
     */
    @GetMapping("/getNearbyAttractions")
    public NearbyAttractionResponse getNearbyAttractions(@RequestParam String userName) throws TrackUserLocationException {
        logger.debug("Getting nearby attractions for user: {}", userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        NearbyAttractionResponse response = tourGuideService.getNearByAttractions(visitedLocation, getUser(userName));
        logger.info("Retrieved nearby attractions for user: {}", userName);
        return response;
    }

    /**
     * Endpoint to retrieve rewards for a user.
     *
     * @param userName The username of the user whose rewards are to be retrieved.
     * @return A list of {@code UserReward} objects for the specified user.
     */
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        logger.debug("Getting rewards for user: {}", userName);
        List<UserReward> rewards = tourGuideService.getUserRewards(getUser(userName));
        logger.info("Retrieved rewards for user: {}", userName);
        return rewards;
    }

    /**
     * Endpoint to retrieve trip deals for a user.
     *
     * @param userName The username of the user whose trip deals are to be retrieved.
     * @return A list of {@code Provider} objects representing trip deals for the user.
     */
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        logger.debug("Getting trip deals for user: {}", userName);
        List<Provider> tripDeals = tourGuideService.getTripDeals(getUser(userName));
        logger.info("Retrieved trip deals for user: {}", userName);
        return tripDeals;
    }

    /**
     * Helper method to retrieve a {@code User} object by username.
     *
     * @param userName The username of the user to retrieve.
     * @return The {@code User} object associated with the given username.
     */
    private User getUser(String userName) {
        logger.debug("Retrieving user for username: {}", userName);
        User user = tourGuideService.getUser(userName);
        logger.info("Retrieved user for username: {}", userName);
        return user;
    }
}
