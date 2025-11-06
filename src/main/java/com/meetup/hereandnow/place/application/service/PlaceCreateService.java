package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.exception.TagErrorCode;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceCreateService {

    private final PlaceRepository placeRepository;
    private final GeometryFactory geometryFactory;
    private final PlaceGroupRepository placeGroupRepository;

    public Place createEntity(PlaceSaveDto dto) {
        Coordinate coord = new Coordinate(dto.placeLongitude(), dto.placeLatitude());
        Point point = geometryFactory.createPoint(coord);

        return Place.builder()
                .placeName(dto.placeName())
                .placeStreetNameAddress(dto.placeStreetNameAddress())
                .placeNumberAddress(dto.placeNumberAddress())
                .location(point)
                .placeGroup(getPlaceGroupByCode(dto.placeGroupCode()))
                .placeCategory(parsePlaceCategory(dto.placeCategory()))
                .build();
    }

    public List<Place> saveAll(List<Place> places) {
        return placeRepository.saveAll(places);
    }

    private PlaceGroup getPlaceGroupByCode(String code) {
        return placeGroupRepository.findByCode(code)
                .orElseThrow(TagErrorCode.PLACE_CODE_NOT_FOUND::toException);
    }

    private String parsePlaceCategory(String fullCategory) {
        if (fullCategory == null) {
            return null;
        }

        String delimiter = " > ";
        int lastIndex = fullCategory.lastIndexOf(delimiter);

        if (lastIndex == -1) {
            return fullCategory;
        } else {
            return fullCategory.substring(lastIndex + delimiter.length());
        }
    }
}
