package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.request.PlaceSaveDto;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceCreateServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private GeometryFactory geometryFactory;

    @Mock
    private PlaceGroupRepository placeGroupRepository;

    @InjectMocks
    private PlaceCreateService placeCreateService;

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_STREET_ADDRESS = "장소 도로명 주소";
    private static final String TEST_PLACE_NUMBER_ADDRESS = "장소 지번 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;
    private static final String TEST_PLACE_CODE = "P03";
    private static final String TEST_PLACE_CATEGORY = "여행 > 공원 > 도시근린공원";
    private static final String TEST_PLACE_URL = "http://place.map.kakao.com/16618597";

    @Test
    @DisplayName("주어진 정보를 통해 place 엔티티를 성공적으로 생성한다.")
    void success_place_create() {
        // given
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(TEST_LON, TEST_LAT);
        Point point = gf.createPoint(coordinate);
        PlaceSaveDto dto = new PlaceSaveDto(
                TEST_PLACE_NAME,
                TEST_PLACE_STREET_ADDRESS,
                TEST_PLACE_NUMBER_ADDRESS,
                TEST_LAT,
                TEST_LON,
                TEST_PLACE_CODE,
                TEST_PLACE_CATEGORY,
                TEST_PLACE_URL
        );

        when(geometryFactory.createPoint(any(Coordinate.class))).thenReturn(point);
        given(placeGroupRepository.findByCode("P03")).willReturn(
                Optional.ofNullable(PlaceGroup.builder().id(1L).code("P03").name("공공기관").build())
        );

        // when
        Place result = placeCreateService.createEntity(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaceName()).isEqualTo(TEST_PLACE_NAME);
        assertThat(result.getPlaceStreetNameAddress()).isEqualTo(TEST_PLACE_STREET_ADDRESS);

        Point location = result.getLocation();
        assertThat(location.getX()).isEqualTo(TEST_LON);
        assertThat(location.getY()).isEqualTo(TEST_LAT);
    }


    @Test
    @DisplayName("Place 엔티티 리스트를 성공적으로 저장하고 저장된 리스트를 반환한다.")
    void success_return_place_list() {
        // given
        Place place1 = Place.builder()
                .placeName(TEST_PLACE_NAME)
                .placeStreetNameAddress(TEST_PLACE_STREET_ADDRESS)
                .placeNumberAddress(TEST_PLACE_NUMBER_ADDRESS)
                .build();

        Place place2 = Place.builder()
                .placeName("장소 2")
                .placeStreetNameAddress("장소 2 도로명 주소")
                .placeNumberAddress("장소 2 지번 주소")
                .build();

        List<Place> placesToSave = List.of(place1, place2);

        when(placeRepository.saveAll(placesToSave)).thenReturn(placesToSave);

        // when
        List<Place> result = placeCreateService.saveAll(placesToSave);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.getFirst().getPlaceName()).isEqualTo(TEST_PLACE_NAME);
        assertThat(result.getFirst().getPlaceStreetNameAddress()).isEqualTo(TEST_PLACE_STREET_ADDRESS);
    }
}