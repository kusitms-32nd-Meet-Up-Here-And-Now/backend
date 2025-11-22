package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PinTagSaveService {

    private final PinTagRepository pinTagRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void savePinTags(List<Pin> pins, List<PinSaveDto> dtos) {

        // PinSaveDto들에서 groupCode별 tagName 수집
        Map<String, Set<String>> tagNamesByGroupCode = new HashMap<>();

        for (PinSaveDto dto : dtos) {
            List<String> tagNames = dto.pinTagNames();
            if (!CollectionUtils.isEmpty(tagNames)) {
                String groupCode = dto.place().placeGroupCode();
                tagNamesByGroupCode
                        .computeIfAbsent(groupCode, k -> new HashSet<>())
                        .addAll(tagNames);
            }
        }

        if (!tagNamesByGroupCode.isEmpty()) {

            // placeGroupCode와 tagName으로 모든 Tag 엔티티 찾음
            List<Tag> allTagList = getAllTagEntities(tagNamesByGroupCode);

            // 한꺼번에 불러온 Tag 리스트에서 알맞은 Tag 엔티티 찾기 위한 Map
            Map<String, Tag> tagMap = allTagList.stream()
                    .collect(Collectors.toMap(
                            // 키 == CT1:야경이 예뻐요
                            tag -> buildTagKey(tag.getPlaceGroup().getCode(), tag.getTagValue().getName()),
                            // 값 == 해당 Tag 엔티티
                            tag -> tag,
                            (tag1, tag2) -> tag1
                    ));

            // 저장할 PinTag
            List<PinTag> pinTagsToSave = new ArrayList<>();

            for (int i = 0; i < pins.size(); i++) {
                Pin pin = pins.get(i);
                PinSaveDto dto = dtos.get(i);
                pinTagsToSave.addAll(createPinTags(pin, dto, tagMap));
            }

            pinTagRepository.saveAll(pinTagsToSave);
        }
    }

    // 필요한 모든 태그 엔티티 한번에 조회
    private List<Tag> getAllTagEntities(Map<String, Set<String>> tagNamesByGroupCode) {
        List<Tag> allTagList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : tagNamesByGroupCode.entrySet()) {
            allTagList.addAll(
                    tagRepository.findByPlaceGroupCodeAndTagNames(entry.getKey(), entry.getValue())
            );
        }
        return allTagList;
    }

    private List<PinTag> createPinTags(Pin pin, PinSaveDto dto, Map<String, Tag> tagMap) {
        if (dto.pinTagNames().isEmpty()) {
            return Collections.emptyList();
        }

        List<PinTag> pinTags = new ArrayList<>();

        for (String tagName : dto.pinTagNames()) {
            String key = buildTagKey(dto.place().placeGroupCode(), tagName);
            Tag tag = tagMap.get(key);
            if (tag != null) {
                pinTags.add(PinTag.of(tag, pin));
            }
        }
        return pinTags;
    }

    // CT1:야경이 예뻐요 <- 같은 태그 키를 생성
    private String buildTagKey(String placeGroupCode, String tagName) {
        return placeGroupCode + ":" + tagName;
    }
}
