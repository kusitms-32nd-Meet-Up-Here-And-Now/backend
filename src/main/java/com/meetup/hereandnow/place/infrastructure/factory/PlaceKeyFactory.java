package com.meetup.hereandnow.place.infrastructure.factory;

import org.springframework.stereotype.Component;

@Component
public class PlaceKeyFactory {

    public String buildKey(String name, double lat, double lon) {
        return name + "|" + lat + "|" + lon;
    }
}
