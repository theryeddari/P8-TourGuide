package com.openclassrooms.tourguide.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;

}
