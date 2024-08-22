package com.openclassrooms.tourguide.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * DTO class for the response containing information about nearby attractions.
 */
@Getter
@Setter
@AllArgsConstructor
public class NearbyAttractionResponse {

    private static final Logger logger = LogManager.getLogger(NearbyAttractionResponse.class);
    /**
     * List of information about nearby attractions.
     */
    private List<InfoAboutNearbyAttractionDTO> attractions;

    /**
     * Default constructor required by Lombok.
     */
    public NearbyAttractionResponse() {
        // Empty constructor for Lombok
        logger.debug("Empty constructor for Lombok called.");
    }

    // Consider adding further methods or documentation here if needed.
}
