package com.meetup.hereandnow.place.application;

import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.PlaceRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceSaveFacade {

    private final PlaceRepository placeRepository;

    /**
     * 입력된 핀 리스트에서 장소 정보를 추출하여 기존 장소는 재사용하고, 신규 장소는 batch로 저장한 뒤
     * key(이름|lat|lon) -> Place 매핑을 반환합니다.
     */
    public Map<String, Place> findOrCreatePlaces(List<PinSaveDto> pinSaveDtos) {
        GeometryFactory geometryFactory = new GeometryFactory();

        Map<String, Place> placeMap = new HashMap<>();
        List<Place> placesToSave = new ArrayList<>();

        for (PinSaveDto pinSaveDto : pinSaveDtos) {
            var placeDto = pinSaveDto.place();
            String key = buildKey(placeDto.placeName(), placeDto.placeLatitude(), placeDto.placeLongitude());
            if (placeMap.containsKey(key)) {
                continue;
            }

            Optional<Place> existing = placeRepository.findByNameAndCoordinates(
                    placeDto.placeName(), placeDto.placeLatitude(), placeDto.placeLongitude());

            if (existing.isPresent()) {
                placeMap.put(key, existing.get());
            } else {
                Coordinate coord = new Coordinate(placeDto.placeLongitude(), placeDto.placeLatitude());
                Point point = geometryFactory.createPoint(coord);

                Place place = Place.builder()
                        .placeName(placeDto.placeName())
                        .placeAddress(placeDto.placeAddress())
                        .location(point)
                        .build();

                placesToSave.add(place);
                placeMap.put(key, place);
            }
        }

        if (!placesToSave.isEmpty()) {
            List<Place> saved = placeRepository.saveAll(placesToSave);
            for (Place sp : saved) {
                String key = buildKey(sp.getPlaceName(), sp.getLocation().getY(), sp.getLocation().getX());
                placeMap.put(key, sp);
            }
        }

        return placeMap;
    }

    private String buildKey(String name, double lat, double lon) {
        return name + "|" + lat + "|" + lon;
    }
}

