package com.openclassrooms.tourguide.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for information about nearby attractions.
 */
@Getter
@Setter
@AllArgsConstructor
public class InfoAboutNearbyAttractionDTO {

    /**
     * The name of the attraction.
     */
    private String attractionName;

    /**
     * The coordinates (X, Y) of the attraction.
     */
    private List<Map<String, Double>> attractionLocationXY;

    /**
     * The coordinates (X, Y) of the user's location.
     */
    private List<Map<String, Double>> userLocationXY;

    /**
     * The distance from the user's location to the attraction.
     */
    private double attractionDistance;

    /**
     * The reward points associated with visiting the attraction.
     */
    private int rewardPoints;

}
