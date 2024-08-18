package com.openclassrooms.tourguide.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InfoAboutNearbyAttractionDTO {
    String attractionName;
    Map<String,Long> attractionLocationXY;
    Map<String,Long> userLocationXY;
    int rewardPoints;
}
