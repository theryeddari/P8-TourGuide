package com.openclassrooms.tourguide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;
	private int rewardPoints;
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}
	
}
