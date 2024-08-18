package com.openclassrooms.tourguide.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NearbyAttractionResponse {

    public NearbyAttractionResponse() {
        //Empty constructor for lombok
    }
    List<InfoAboutNearbyAttractionDTO> attractions;
}

