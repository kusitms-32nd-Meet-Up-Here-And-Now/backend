package com.meetup.hereandnow.place.application.facade;

import com.meetup.hereandnow.pin.domain.value.PinTagEnum;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.application.service.PlaceCreateService;
import com.meetup.hereandnow.place.application.service.PlaceFindService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import com.meetup.hereandnow.place.infrastructure.factory.PlaceKeyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceSaveFacadeTest {

    @InjectMocks
    private PlaceSaveFacade placeSaveFacade;
    @Mock
    private PlaceFindService placeFindService;
    @Mock
    private PlaceCreateService placeCreateService;
    @Mock
    private PlaceKeyFactory placeKeyFactory;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @DisplayName("새로운 장소 목록이 주어지면 장소를 생성하고 반환한다")
    @Test
    void success_new_place_create_and_return() {
        // given
        PlaceSaveDto placeDto1 = new PlaceSaveDto("placeName1", "address1", 37.123, 127.123);
        PlaceSaveDto placeDto2 = new PlaceSaveDto("placeName2", "address2", 37.456, 127.456);
        PinSaveDto pinSaveDto1 = new PinSaveDto("핀 제목 1", 4.5, "핀 설명 1", List.of(PinTagEnum.COZY), placeDto1);
        PinSaveDto pinSaveDto2 = new PinSaveDto("핀 제목 2", 4.5, "핀 설명 2", List.of(PinTagEnum.EXCITED), placeDto2);
        List<PinSaveDto> pinSaveDtos = List.of(pinSaveDto1, pinSaveDto2);

        Point point1 = geometryFactory.createPoint(new Coordinate(127.123, 37.123));
        Place newPlace1 = Place.builder().placeName("placeName1").placeAddress("address1").location(point1).build();
        Point point2 = geometryFactory.createPoint(new Coordinate(127.456, 37.456));
        Place newPlace2 = Place.builder().placeName("placeName2").placeAddress("address2").location(point2).build();
        List<Place> placesToSave = List.of(newPlace1, newPlace2);

        given(placeKeyFactory.buildKey("placeName1", 37.123, 127.123)).willReturn("key1");
        given(placeKeyFactory.buildKey("placeName2", 37.456, 127.456)).willReturn("key2");
        given(placeFindService.findByNameAndCoordinates(anyString(), anyDouble(), anyDouble())).willReturn(Optional.empty());
        given(placeCreateService.createEntity("placeName1", "address1", 37.123, 127.123)).willReturn(newPlace1);
        given(placeCreateService.createEntity("placeName2", "address2", 37.456, 127.456)).willReturn(newPlace2);
        given(placeCreateService.saveAll(placesToSave)).willReturn(placesToSave);

        // when
        Map<String, Place> result = placeSaveFacade.findOrCreatePlaces(pinSaveDtos);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("key1", newPlace1);
        assertThat(result).containsEntry("key2", newPlace2);
        verify(placeCreateService).saveAll(placesToSave);
    }

    @DisplayName("기존 장소 목록이 주어지면 장소를 조회하고 반환한다")
    @Test
    void success_return_existing_place() {
        // given
        PlaceSaveDto placeDto = new PlaceSaveDto("placeName", "address", 37.123, 127.123);
        PinSaveDto pinSaveDto = new PinSaveDto("핀 제목 1", 4.5, "핀 설명 1", List.of(PinTagEnum.COZY), placeDto);
        List<PinSaveDto> pinSaveDtos = List.of(pinSaveDto);
        Point point = geometryFactory.createPoint(new Coordinate(127.123, 37.123));
        Place existingPlace = Place.builder().placeName("placeName").placeAddress("address").location(point).build();

        given(placeKeyFactory.buildKey("placeName", 37.123, 127.123)).willReturn("key1");
        given(placeFindService.findByNameAndCoordinates("placeName", 37.123, 127.123)).willReturn(Optional.of(existingPlace));

        // when
        Map<String, Place> result = placeSaveFacade.findOrCreatePlaces(pinSaveDtos);

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsEntry("key1", existingPlace);
        verify(placeCreateService, never()).saveAll(any());
    }

    @DisplayName("빈 목록이 주어지면 빈 맵을 반환한다")
    @Test
    void success_empty_list_return_empty_map() {
        // given
        List<PinSaveDto> pinSaveDtos = Collections.emptyList();

        // when
        Map<String, Place> result = placeSaveFacade.findOrCreatePlaces(pinSaveDtos);

        // then
        assertThat(result).isEmpty();
        verify(placeFindService, never()).findByNameAndCoordinates(anyString(), anyDouble(), anyDouble());
        verify(placeCreateService, never()).saveAll(any());
    }
}
