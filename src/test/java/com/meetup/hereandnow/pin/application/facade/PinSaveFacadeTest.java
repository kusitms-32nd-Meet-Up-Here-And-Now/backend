package com.meetup.hereandnow.pin.application.facade;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.pin.application.service.save.PinImageSaveService;
import com.meetup.hereandnow.pin.application.service.save.PinSaveService;
import com.meetup.hereandnow.pin.application.service.save.PinTagSaveService;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
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

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinSaveFacadeTest {

    @Mock
    private PinSaveService pinSaveService;

    @Mock
    private PinTagSaveService pinTagSaveService;

    @Mock
    private PinImageSaveService pinImageSaveService;

    @InjectMocks
    private PinSaveFacade pinSaveFacade;

    private Course dummyCourse;

    private Place dummyPlace;

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_STREET_ADDRESS = "장소 도로명 주소";
    private static final String TEST_PLACE_NUMBER_ADDRESS = "장소 지번 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;
    private static final String TEST_PLACE_CODE = "P03";
    private static final String TEST_PLACE_CATEGORY = "여행 > 공원 > 도시근린공원";
    private static final String TEST_PLACE_URL = "http://place.map.kakao.com/16618597";

    private static final String TEST_PIN_POSITIVE = "핀 좋은 점";
    private static final String TEST_PIN_NEGATIVE = "핀 나쁜 점";
    private static final double TEST_PIN_RATING = 4.5;

    private static final String TEST_OBJECT_KEY = "/img/1.jpg";
    private static final int TEST_PIN_INDEX = 0;

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
    @DisplayName("핀의 모든 내용이 성공적으로 저장된다.")
    void success_save_pin_calls_all_services() {

        // given
        PlaceSaveDto placeDto = new PlaceSaveDto(
                TEST_PLACE_NAME,
                TEST_PLACE_STREET_ADDRESS,
                TEST_PLACE_NUMBER_ADDRESS,
                TEST_LAT,
                TEST_LON,
                TEST_PLACE_CODE,
                TEST_PLACE_CATEGORY,
                TEST_PLACE_URL
        );

        PinSaveDto pinDto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                List.of(),
                placeDto
        );

        Pin savedPin = Pin.builder().id(10L).build();
        when(pinSaveService.savePins(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace)))
                .thenReturn(List.of(savedPin));

        PinImageObjectKeyDto imageDto = new PinImageObjectKeyDto(TEST_PIN_INDEX, List.of(TEST_OBJECT_KEY));
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(List.of(imageDto));

        // when
        pinSaveFacade.savePinEntityToTable(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace), commitDto);

        // then
        verify(pinSaveService).savePins(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace));
        verify(pinTagSaveService).savePinTags(List.of(savedPin), List.of(pinDto));
        verify(pinImageSaveService).savePinImages(List.of(savedPin), List.of(imageDto));
    }
}

