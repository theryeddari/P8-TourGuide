package com.openclassrooms.tourguide.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;
import tripPricer.Provider;

@Getter
@Setter
public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

    public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

    public void clearVisitedLocations() {
		visitedLocations.clear();
	}
	
	public void addUserReward(UserReward userReward) {

		//Filter all userRewards in the list<UserReward> that do not have an attractionName equal to the attractionName of the userReward passed as a parameter
		//to ensure that there are none. If this is the case the condition is respected, otherwise nothing
		if(userRewards.stream().noneMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName))) {
			userRewards.add(userReward);
		}
	}

	public VisitedLocation getLastVisitedLocation() {
		return visitedLocations.getLast();
	}

}
