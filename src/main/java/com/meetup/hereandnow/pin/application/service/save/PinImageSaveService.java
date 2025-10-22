package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinImageSaveService {

    private final PinImageRepository pinImageRepository;

    public void savePinImages(List<Pin> pinList, List<PinImageObjectKeyDto> pinImageObjectKeyDtoList) {
        List<PinImage> pinImagesToSave = IntStream.range(0, pinList.size())
                .mapToObj(i -> {
                    Pin savedPin = pinList.get(i);
                    List<String> objectKeys = pinImageObjectKeyDtoList.get(i).objectKeyList();

                    if (objectKeys == null || objectKeys.isEmpty()) {
                        return Stream.<PinImage>empty();
                    }

                    return objectKeys.stream()
                            .map(objectKey -> PinImage.of(objectKey, savedPin));
                })
                .flatMap(Function.identity())
                .toList();

        if (pinImagesToSave.isEmpty()) {
            return;
        }

        pinImageRepository.saveAll(pinImagesToSave);
    }
}
