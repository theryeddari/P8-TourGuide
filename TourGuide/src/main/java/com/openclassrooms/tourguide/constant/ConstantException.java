package com.openclassrooms.tourguide.constant;

/**
 * The {@code ConstantException} class contains string constants used for exception handling
 * in the application's services. These strings are used to provide meaningful error messages
 * when operations fail.
 */
public class ConstantException {

    /**
     * Exception message for the RewardsService.
     * Used when reward calculation fails.
     */
    public static final String CALCULATE_REWARDS_EXCEPTION = "An error occurred while trying to calculate the rewards.";

    /**
     * Exception message for the TourGuideService.
     * Used when tracking the user's location fails.
     */
    public static final String TRACK_USER_LOCATION_EXCEPTION = "An error occurred while trying to calculate the track user location.";

    // You may add static methods to handle exceptions if needed,
    // but that is beyond the scope of the current constants.
}