package com.openclassrooms.tourguide.controller;

import java.util.List;

import com.openclassrooms.tourguide.dto.NearbyAttractionResponse;
import exception.TourGuideServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.model.user.User;
import com.openclassrooms.tourguide.model.user.UserReward;

import tripPricer.Provider;

import static exception.TourGuideServiceException.*;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @GetMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) throws TrackUserLocationException {
    	return tourGuideService.getUserLocation(getUser(userName));
    }


    @GetMapping("/getNearbyAttractions")
    public NearbyAttractionResponse getNearbyAttractions(@RequestParam String userName) throws TrackUserLocationException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearByAttractions(visitedLocation, getUser(userName));
    }
    
    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}