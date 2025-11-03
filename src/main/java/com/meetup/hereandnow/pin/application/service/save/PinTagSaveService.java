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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PinTagSaveService {

    private final PinTagRepository pinTagRepository;
    private final TagRepository tagRepository;

    private static final String TAG_MAP_KEY_DELIMITER = "::";

    public void savePinTags(List<Pin> pinList, List<PinSaveDto> pinSaveDtos) {

        Set<String> placeGroupCodes = pinSaveDtos.stream()
                .map(PinSaveDto::placeGroupCode)
                .collect(Collectors.toSet());

        Set<String> tagNames = pinSaveDtos.stream()
                .filter(dto -> dto.pinTagNames() != null)
                .flatMap(dto -> dto.pinTagNames().stream())
                .collect(Collectors.toSet());

        if (placeGroupCodes.isEmpty() || tagNames.isEmpty()) {
            return;
        }

        List<Tag> allTags = tagRepository.findByPlaceGroupCodesAndTagNames(placeGroupCodes, tagNames);

        Map<String, Tag> tagMap = allTags.stream()
                .collect(Collectors.toMap(
                        this::createTagMapKey,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<PinTag> pinTagsToSave = IntStream.range(0, pinList.size())
                .boxed()
                .flatMap(i -> {
                    Pin pin = pinList.get(i);
                    PinSaveDto dto = pinSaveDtos.get(i);
                    String placeGroupCode = dto.placeGroupCode();
                    List<String> tagList = dto.pinTagNames();

                    if (tagList == null || tagList.isEmpty()) {
                        return Stream.empty();
                    }

                    return tagList.stream()
                            .distinct()
                            .map(tagName -> {
                                String key = createTagMapKey(placeGroupCode, tagName);
                                Tag foundTag = tagMap.get(key);
                                if (foundTag == null) {
                                    throw TagErrorCode.NOT_FOUND_TAG_DATA.toException();
                                }
                                return PinTag.of(foundTag, pin);
                            });
                })
                .toList();

        if (pinTagsToSave.isEmpty()) {
            return;
        }

        pinTagRepository.saveAll(pinTagsToSave);
    }

    private String createTagMapKey(Tag tag) {
        return tag.getPlaceGroup().getCode() + TAG_MAP_KEY_DELIMITER + tag.getTagValue().getName();
    }

    private String createTagMapKey(String placeGroupCode, String tagName) {
        return placeGroupCode + TAG_MAP_KEY_DELIMITER + tagName;
    }
}
