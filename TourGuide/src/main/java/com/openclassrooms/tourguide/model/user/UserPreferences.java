package com.openclassrooms.tourguide.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents user preferences for planning a trip or visit.
 * <p>
 * This class contains information about the user's preferences regarding
 * attractions, trip duration, ticket quantity, and the number of adults and
 * children for a given trip.
 * </p>
 * <p>
 * All fields have default values:
 * <ul>
 *   <li>{@code attractionProximity} is set to {@link Integer#MAX_VALUE}.</li>
 *   <li>{@code tripDuration} is set to 1.</li>
 *   <li>{@code ticketQuantity} is set to 1.</li>
 *   <li>{@code numberOfAdults} is set to 1.</li>
 *   <li>{@code numberOfChildren} is set to 0.</li>
 * </ul>
 * </p>
 */
@Setter
@Getter
@NoArgsConstructor
public class UserPreferences {

    /**
     * The maximum distance (in some unit) within which attractions are considered
     * for the trip. Defaults to {@link Integer#MAX_VALUE}, meaning no proximity limit.
     */
    private int attractionProximity = Integer.MAX_VALUE;

    /**
     * The duration of the trip in days. Defaults to 1 day.
     */
    private int tripDuration = 1;

    /**
     * The number of tickets required for the trip. Defaults to 1 ticket.
     */
    private int ticketQuantity = 1;

    /**
     * The number of adults included in the trip. Defaults to 1 adult.
     */
    private int numberOfAdults = 1;

    /**
     * The number of children included in the trip. Defaults to 0 children.
     */
    private int numberOfChildren = 0;

}