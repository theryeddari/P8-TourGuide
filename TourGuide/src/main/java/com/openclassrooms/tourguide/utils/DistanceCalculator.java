package com.openclassrooms.tourguide.utils;

public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0; // Rayon moyen de la Terre en kilomètres
    private static final double KM_TO_MILES_CONVERSION = 0.621371; // Conversion factor from kilometers to miles

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert coordinates from degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon1 - lon2);

        // Apply the Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance in kilometers
        double distanceInKm = EARTH_RADIUS_KM * c;

        // Convert to miles
        double distanceInMiles = distanceInKm * KM_TO_MILES_CONVERSION;

        return distanceInMiles;
    }
}