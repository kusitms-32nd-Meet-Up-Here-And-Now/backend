package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.pin.domain.value.PinTagEnum;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinTagSaveService {

    private final PinTagRepository pinTagRepository;

    public void savePinTags(List<Pin> pinList, List<PinSaveDto> pinSaveDtos) {
        List<PinTag> pinTagsToSave = IntStream.range(0, pinList.size())
                .boxed()
                .flatMap(i -> {
                    Pin pin = pinList.get(i);
                    List<PinTagEnum> tagEnums = pinSaveDtos.get(i).pinTags();

                    if (tagEnums == null || tagEnums.isEmpty()) {
                        return Stream.<PinTag>empty();
                    }

                    return tagEnums.stream()
                            .distinct()
                            .map(tagEnum -> PinTag.of(tagEnum, pin));
                })
                .toList();

        if (pinTagsToSave.isEmpty()) {
            return;
        }

        pinTagRepository.saveAll(pinTagsToSave);
    }
}
