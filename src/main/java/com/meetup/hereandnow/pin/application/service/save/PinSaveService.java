package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.factory.PlaceKeyFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinSaveService {

    private final PinRepository pinRepository;
    private final PlaceKeyFactory placeKeyFactory;

    public List<Pin> savePins(List<PinSaveDto> pinSaveDtos, Course course, Map<String, Place> placeMap) {
        List<Pin> pinsToSave = new ArrayList<>();

        for (PinSaveDto dto : pinSaveDtos) {
            var placeDto = dto.place();
            String key = placeKeyFactory.buildKey(
                    placeDto.placeName(),
                    placeDto.placeLatitude(),
                    placeDto.placeLongitude()
            );

            Place place = placeMap.get(key);

            if (place == null) {
                throw PinErrorCode.NOT_FOUND_PIN_IMAGE.toException();
            }

            Pin pin = Pin.builder()
                    .pinTitle(dto.pinTitle())
                    .pinDescription(dto.pinDescription())
                    .pinRating(BigDecimal.valueOf(4.5))
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
