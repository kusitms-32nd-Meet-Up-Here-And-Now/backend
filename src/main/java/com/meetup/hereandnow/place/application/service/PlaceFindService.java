package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceFindService {

    private final PlaceRepository placeRepository;

    public Optional<Place> findByNameAndCoordinates(String name, double lat, double lon) {
        return placeRepository.findByNameAndCoordinates(name, lat, lon);
    }
}
