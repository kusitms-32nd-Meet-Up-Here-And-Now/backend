package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import com.meetup.hereandnow.place.infrastructure.factory.PlaceKeyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinSaveServiceTest {

    @Mock
    private PinRepository pinRepository;

    @Mock
    private PlaceKeyFactory placeKeyFactory;

    @InjectMocks
    private PinSaveService pinSaveService;

    private Course dummyCourse;

    private Place dummyPlace;

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_STREET_ADDRESS = "장소 도로명 주소";
    private static final String TEST_PLACE_NUMBER_ADDRESS = "장소 지번 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private static final String TEST_PIN_POSITIVE = "핀 좋은 점";
    private static final String TEST_PIN_NEGATIVE = "핀 나쁜 점";
    private static final String TEST_PLACE_CODE = "CT1";
    private static final double TEST_PIN_RATING = 4.5;

    @BeforeEach
    void setUp() {
        dummyCourse = Course.builder().id(1L).build();
        GeometryFactory geometryFactory = new GeometryFactory();

        Coordinate coord = new Coordinate(TEST_LON, TEST_LAT);
        Point point = geometryFactory.createPoint(coord);

        dummyPlace = Place.builder()
                .placeName(TEST_PLACE_NAME)
                .placeStreetNameAddress(TEST_PLACE_STREET_ADDRESS)
                .placeNumberAddress(TEST_PLACE_NUMBER_ADDRESS)
                .location(point)
                .build();
    }

    @Test
    @DisplayName("핀 정보가 없는 경우 빈 리스트를 반환한다.")
    void success_no_pins_dummy_result() {

        // given & when
        List<Pin> result = pinSaveService.savePins(List.of(), dummyCourse, Map.of());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("핀이 정상적으로 저장된다.")
    void success_save_pins() {

        // given
        PlaceSaveDto placeDto = new PlaceSaveDto(
                TEST_PLACE_NAME,
                TEST_PLACE_STREET_ADDRESS,
                TEST_PLACE_NUMBER_ADDRESS,
                TEST_LAT,
                TEST_LON
        );

        PinSaveDto dto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                placeDto
        );
        Map<String, Place> placeMap = Map.of(TEST_PLACE_NAME + "|" + TEST_LAT + "|" + TEST_LON, dummyPlace);

        when(pinRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(placeKeyFactory.buildKey(TEST_PLACE_NAME, TEST_LAT, TEST_LON))
                .thenReturn(TEST_PLACE_NAME + "|" + TEST_LAT + "|" + TEST_LON);

        // when
        List<Pin> saved = pinSaveService.savePins(List.of(dto), dummyCourse, placeMap);

        // then
        assertThat(saved).hasSize(1);
        Pin pin = saved.getFirst();
        assertThat(pin.getPinRating()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
        assertThat(pin.getCourse()).isSameAs(dummyCourse);
        assertThat(pin.getPlace()).isSameAs(dummyPlace);

        verify(pinRepository).saveAll(anyList());
    }
}

