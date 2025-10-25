package com.meetup.hereandnow.place.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceFindServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceFindService placeFindService;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private final String TEST_NAME = "테스트 장소";
    private final String TEST_ADDRESS = "테스트 주소";
    private final double TEST_LAT = 37.5665;
    private final double TEST_LON = 127.9780;

    @Test
    @DisplayName("Place 객체 1개가 정상적으로 반환된다.")
    void success_find_place() {
        // given
        Coordinate coord = new Coordinate(TEST_LON, TEST_LAT);
        Point point = geometryFactory.createPoint(coord);

        Place expected = Place.builder()
                .placeName(TEST_NAME)
                .placeAddress(TEST_ADDRESS)
                .location(point)
                .build();

        when(placeRepository.findByNameAndCoordinates(TEST_NAME, TEST_LAT, TEST_LON)).thenReturn(Optional.of(expected));

        // when
        Optional<Place> result = placeFindService.findByNameAndCoordinates(TEST_NAME, TEST_LAT, TEST_LON);

        // then
        assertThat(result).isPresent();
        assertThat(result).contains(expected);
    }

}