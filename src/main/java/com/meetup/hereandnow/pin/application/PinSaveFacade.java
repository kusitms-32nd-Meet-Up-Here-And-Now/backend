package com.meetup.hereandnow.pin.application;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import com.meetup.hereandnow.pin.infrastructure.PinRepository;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.domain.Place;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinSaveFacade {

    private final PinRepository pinRepository;
    private final ObjectStorageService objectStorageService;

    public void validatePinImagesExist(List<PinImageObjectKeyDto> pinImageObjectKeyDtoList) {
        pinImageObjectKeyDtoList.stream()
                .map(PinImageObjectKeyDto::objectKeyList)
                .flatMap(Collection::stream)
                .parallel()
                .forEach(key -> {
                    if (!objectStorageService.exists(key)) {
                        throw PinErrorCode.NOT_FOUND_PIN_IMAGE.toException();
                    }
                });
    }

    /**
     * 주어진 핀 DTO 리스트와 placeMap을 이용해 Pin 엔티티를 생성하고 batch로 저장합니다. 반환값은 저장된 Pin 리스트입니다.
     */
    public List<Pin> savePins(List<PinSaveDto> pinSaveDtos, Course course, Map<String, Place> placeMap) {
        List<Pin> pinsToSave = new ArrayList<>();

        for (PinSaveDto dto : pinSaveDtos) {
            var placeDto = dto.place();
            String key = placeDto.placeName() + "|" + placeDto.placeLatitude() + "|" + placeDto.placeLongitude();
            Place place = placeMap.get(key);

            Pin pin = Pin.builder()
                    .pinTitle(dto.pinTitle())
                    .pinDescription(dto.pinDescription())
                    .pinRating(BigDecimal.valueOf(dto.pinRating()))
                    .course(course)
                    .place(place)
                    .build();

            pinsToSave.add(pin);
        }

        if (pinsToSave.isEmpty()) {
            return List.of();
        }

        return pinRepository.saveAll(pinsToSave);
    }
}

