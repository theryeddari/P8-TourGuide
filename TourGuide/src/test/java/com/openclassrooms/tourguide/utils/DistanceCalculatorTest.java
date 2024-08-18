package com.openclassrooms.tourguide.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void calculateDistance() {
        //Distance between two identical points (should be 0)
        double distance = DistanceCalculator.calculateDistance(48.8566, 2.3522, 48.8566, 2.3522);
        assertEquals(0.0, distance, 0.01, "Distance between identical points should be 0");

        // Distance between Paris (48.8566, 2.3522) and London (51.5074, -0.1278)
        distance = DistanceCalculator.calculateDistance(48.8566, 2.3522, 51.5074, -0.1278);
        assertEquals(213.0, distance, 5.0, "Distance between Paris and London should be approximately 213 miles");
    }
}