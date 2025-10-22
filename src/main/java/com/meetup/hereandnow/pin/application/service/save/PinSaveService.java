package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
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
