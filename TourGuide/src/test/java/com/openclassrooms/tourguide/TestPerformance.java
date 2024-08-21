package com.openclassrooms.tourguide;

import static exception.TourGuideServiceException.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import exception.RewardsServiceException;
import exception.TourGuideServiceException;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.utils.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.model.user.User;

public class TestPerformance {

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Test
	public void highVolumeTrackLocation() {
		// Initialize services
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes
		InternalTestHelper.setInternalUserNumber(100000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		// Get list of users
		List<User> allUsers = tourGuideService.getAllUsers();

		// Start timing
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// Track all user locations asynchronously and collect the futures into a list
		List<CompletableFuture<VisitedLocation>> futures = allUsers.stream()
				.map(user -> {
                    try {
                        return tourGuideService.trackUserLocation(user);
                    } catch (TrackUserLocationException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

		// Wait for all futures to complete
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		// Stop timing
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		// Output the time elapsed
		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

		// Assert that the test finishes within 15 minutes
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards(){
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		Attraction attraction = gpsUtil.getAttractions().getFirst();
		List<User> allUsers = tourGuideService.getAllUsers();

		// Add a visited location for each user
		allUsers.forEach(user -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date())));

		// Calculate rewards asynchronously for each user
		// Using CompletableFuture to handle asynchronous reward calculation
		List<CompletableFuture<Void>> futures = allUsers.stream()
				.map(user -> CompletableFuture.runAsync(() -> {
                    try {
                        rewardsService.calculateRewards(user);
                    } catch (RewardsServiceException.CalulateRewardsException e) {
                        throw new RuntimeException(e);
                    }
                })).toList();

		// Wait for all futures to complete
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join(); // Blocks until all futures are complete

		// Check that rewards have been calculated for each user
		for (User user : allUsers) {
			assertFalse(user.getUserRewards().isEmpty());
		}

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		// Print elapsed time and assert that it completes within 20 minutes
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
