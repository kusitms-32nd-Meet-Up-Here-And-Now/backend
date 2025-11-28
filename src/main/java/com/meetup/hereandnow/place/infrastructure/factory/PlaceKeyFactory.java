package com.meetup.hereandnow.place.infrastructure.factory;

import org.springframework.stereotype.Component;

@Component
public class PlaceKeyFactory {

    public String buildKey(String name, double lat, double lon) {
        String escapeName = name.replace("|", "\\|");
        return String.format("%s|%.6f|%.6f", escapeName, lat, lon);
    }
}
