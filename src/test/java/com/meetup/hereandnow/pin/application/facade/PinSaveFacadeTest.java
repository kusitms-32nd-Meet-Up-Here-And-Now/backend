package com.meetup.hereandnow.pin.application.facade;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Map;
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
    private static final String TEST_PLACE_ADDRESS = "장소 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private static final String TEST_PIN_TITLE = "핀 제목";
    private static final String TEST_PIN_DESC = "핀 설명";
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
                .placeAddress(TEST_PLACE_ADDRESS)
                .location(point)
                .build();
    }

    @Test
    @DisplayName("핀의 모든 내용이 성공적으로 저장된다.")
    void success_save_pin_calls_all_services() {

        // given
        PlaceSaveDto placeDto = new PlaceSaveDto(TEST_PLACE_NAME, TEST_PLACE_ADDRESS, TEST_LAT, TEST_LON);
        PinSaveDto pinDto = new PinSaveDto(TEST_PIN_TITLE, TEST_PIN_RATING, TEST_PIN_DESC, List.of(), placeDto);

        Pin savedPin = Pin.builder().id(10L).build();
        when(pinSaveService.savePins(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace)))
                .thenReturn(List.of(savedPin));

        PinImageObjectKeyDto imageDto = new PinImageObjectKeyDto(TEST_PIN_INDEX, List.of(TEST_OBJECT_KEY));
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(null, List.of(imageDto));

        // when
        pinSaveFacade.savePinEntityToTable(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace), commitDto);

        // then
        verify(pinSaveService).savePins(List.of(pinDto), dummyCourse, Map.of("장소 이름|37.1|127.1", dummyPlace));
        verify(pinTagSaveService).savePinTags(List.of(savedPin), List.of(pinDto));
        verify(pinImageSaveService).savePinImages(List.of(savedPin), List.of(imageDto));
    }
}

