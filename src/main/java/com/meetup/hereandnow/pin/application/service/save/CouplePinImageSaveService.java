package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.connect.domain.CouplePinImage;
import com.meetup.hereandnow.connect.domain.CouplePinRecord;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.connect.repository.CouplePinImageRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouplePinImageSaveService {

    private final CouplePinImageRepository couplePinImageRepository;

    public void saveCouplePinImages(
            List<CouplePinRecord> couplePinRecords,
            List<PinImageObjectKeyDto> pinImageObjectKeyDtoList
    ) {
        List<CouplePinImage> couplePinImages = IntStream.range(0, couplePinRecords.size())
                .mapToObj(i -> {
                    List<String> objectKeys = pinImageObjectKeyDtoList.get(i).coupleImageObjectKeyList();
                    CouplePinRecord couplePinRecord = couplePinRecords.get(i);

                    if (objectKeys == null || objectKeys.isEmpty()) {
                        return Stream.<CouplePinImage>empty();
                    }

                    return objectKeys.stream()
                            .map(objectKey -> {
                                CouplePinImage couplePinImage = CouplePinImage.of(objectKey, couplePinRecord);
                                couplePinRecord.addCouplePinImage(couplePinImage);
                                return couplePinImage;
                            });
                })
                .flatMap(Function.identity())
                .toList();

        if (couplePinImages.isEmpty()) {
            return;
        }

        couplePinImageRepository.saveAll(couplePinImages);
    }
}
