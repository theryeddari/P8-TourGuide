package com.openclassrooms.tourguide.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class InfoAboutNearbyAttractionDTO {
    String attractionName;
    List<Map<String,Double>> attractionLocationXY;
    List<Map<String,Double>> userLocationXY;
    double attractionDistance;
    int rewardPoints;
}
