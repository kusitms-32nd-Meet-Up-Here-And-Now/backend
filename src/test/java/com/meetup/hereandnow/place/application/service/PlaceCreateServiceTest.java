package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceCreateServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceCreateService placeCreateService;

    private final String TEST_NAME = "테스트 장소";
    private final String TEST_ADDRESS = "테스트 주소";
    private final double TEST_LAT = 37.5665;
    private final double TEST_LON = 127.9780;

    @Test
    @DisplayName("주어진 정보를 통해 place 엔티티를 성공적으로 생성한다.")
    void success_place_create() {
        // given & when
        Place result = placeCreateService.createEntity(TEST_NAME, TEST_ADDRESS, TEST_LAT, TEST_LON);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaceName()).isEqualTo(TEST_NAME);
        assertThat(result.getPlaceAddress()).isEqualTo(TEST_ADDRESS);

        Point location = result.getLocation();
        assertThat(location.getX()).isEqualTo(TEST_LON);
        assertThat(location.getY()).isEqualTo(TEST_LAT);
    }


    @Test
    @DisplayName("Place 엔티티 리스트를 성공적으로 저장하고 저장된 리스트를 반환한다.")
    void success_return_place_list() {
        // given
        Place place1 = Place.builder()
                .placeName(TEST_NAME)
                .placeAddress(TEST_ADDRESS)
                .build();

        Place place2 = Place.builder()
                .placeName("장소 2")
                .placeAddress("주소 2")
                .build();
        List<Place> placesToSave = List.of(place1, place2);

        when(placeRepository.saveAll(placesToSave)).thenReturn(placesToSave);

        // when
        List<Place> result = placeCreateService.saveAll(placesToSave);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.getFirst().getPlaceName()).isEqualTo(TEST_NAME);
        assertThat(result.getFirst().getPlaceAddress()).isEqualTo(TEST_ADDRESS);
    }
}