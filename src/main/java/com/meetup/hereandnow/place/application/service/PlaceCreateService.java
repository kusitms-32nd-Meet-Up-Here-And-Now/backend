package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceCreateService {

    private final PlaceRepository placeRepository;
    private final GeometryFactory geometryFactory;

    public Place createEntity(String name, String streetAddress, String numberAddress, double lat, double lon) {
        Coordinate coord = new Coordinate(lon, lat);
        Point point = geometryFactory.createPoint(coord);

        return Place.builder()
                .placeName(name)
                .placeStreetNameAddress(streetAddress)
                .placeNumberAddress(numberAddress)
                .location(point)
                .build();
    }

    public List<Place> saveAll(List<Place> places) {
        return placeRepository.saveAll(places);
    }
}
