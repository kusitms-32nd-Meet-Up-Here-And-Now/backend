package com.meetup.hereandnow.place.application.facade;

import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.application.service.PlaceCreateService;
import com.meetup.hereandnow.place.application.service.PlaceFindService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.factory.PlaceKeyFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceSaveFacade {

    private final PlaceFindService placeFindService;
    private final PlaceCreateService placeCreateService;
    private final PlaceKeyFactory placeKeyFactory;

    @Transactional
    public Map<String, Place> findOrCreatePlaces(List<PinSaveDto> pinSaveDtos) {
        Map<String, Place> placeMap = new LinkedHashMap<>();
        List<Place> placesToSave = new ArrayList<>();

        for (PinSaveDto pinSaveDto : pinSaveDtos) {
            var placeDto = pinSaveDto.place();
            Objects.requireNonNull(placeDto, "pinSaveDto.place()");
            String key = placeKeyFactory.buildKey(placeDto.placeName(), placeDto.placeLatitude(), placeDto.placeLongitude());

            if (placeMap.containsKey(key)) continue;

            placeFindService.findByNameAndCoordinates(
                    placeDto.placeName(),
                    placeDto.placeLatitude(),
                    placeDto.placeLongitude()
            ).ifPresentOrElse(
                    existing -> placeMap.put(key, existing),
                    () -> {
                        Place newPlace = placeCreateService.createEntity(placeDto);
                        placesToSave.add(newPlace);
                        placeMap.put(key, newPlace);
                    }
            );
        }

        if (!placesToSave.isEmpty()) {
            List<Place> saved = placeCreateService.saveAll(placesToSave);
            saved.forEach(sp -> {
                String key = placeKeyFactory.buildKey(
                        sp.getPlaceName(),
                        sp.getLocation().getY(),
                        sp.getLocation().getX()
                );
                placeMap.put(key, sp);
            });
        }

        return placeMap;
    }
}
