package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.exception.TagErrorCode;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PinTagSaveService {

    private final PinTagRepository pinTagRepository;
    private final TagRepository tagRepository;

    public void savePinTags(List<Pin> pinList, List<PinSaveDto> pinSaveDtos) {
        List<PinTag> pinTagsToSave = IntStream.range(0, pinList.size())
                .boxed()
                .flatMap(i -> {
                    Pin pin = pinList.get(i);
                    List<String> tagList = pinSaveDtos.get(i).pinTagNames();

                    if (tagList == null || tagList.isEmpty()) {
                        return Stream.empty();
                    }

                    return tagList.stream()
                            .distinct()
                            .map(tagName -> PinTag.of(
                                    getTag(pinSaveDtos.get(i).placeGroupCode(), tagName),
                                    pin
                            ));
                })
                .toList();

        if (pinTagsToSave.isEmpty()) {
            return;
        }

        pinTagRepository.saveAll(pinTagsToSave);
    }

    private Tag getTag(String placeGroupCode, String tagName) {
        return tagRepository.findByPlaceGroupAndTagName(placeGroupCode, tagName)
                .orElseThrow(TagErrorCode.NOT_FOUND_TAG_DATA::toException);
    }
}
