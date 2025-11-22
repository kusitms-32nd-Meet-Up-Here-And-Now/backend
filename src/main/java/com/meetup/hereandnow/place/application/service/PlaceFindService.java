package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceFindService {

    private final PlaceRepository placeRepository;

    public Optional<Place> findByNameAndCoordinates(String name, double lat, double lon) {
        return placeRepository.findByNameAndCoordinates(name, lat, lon);
    }

    public List<Place> findNearbyPlaces(double lat, double lon, Pageable pageable) {
        return placeRepository.findPlacesByLocation(lat, lon, pageable).stream().toList();
    }

    public List<Place> find2RandomNearbyPlaces(double lat, double lon) {
        List<Long> nearbyIds = placeRepository.findNearbyPlaceIds(lat, lon);
        if (nearbyIds.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(nearbyIds);
        List<Long> randomIds = nearbyIds.subList(0, Math.min(nearbyIds.size(), 2));
        return placeRepository.findAllById(randomIds);
    }
}
