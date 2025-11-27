package com.meetup.hereandnow.integration.support;

import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.entity.TagValue;
import com.meetup.hereandnow.tag.domain.value.TagGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagValueRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("test")
@RequiredArgsConstructor
public class TestTagInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final PlaceGroupRepository placeGroupRepository;
    private final TagRepository tagRepository;
    private final TagValueRepository tagValueRepository;

    private final Map<String, TagValue> tagValueMap = new HashMap<>();

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        String placeCode = "FD6";
        if (placeGroupRepository.findByCode(placeCode).isEmpty()) {
            PlaceGroup fd6 = placeGroupRepository.save(PlaceGroup.builder().code("FD6").name("음식점").build());
            link(fd6, TagGroup.FOOD_PRICE, "음식이 맛있어요", "메뉴가 다양해요", "특별한 메뉴가 있어요");
            link(fd6, TagGroup.ATMOSPHERE, "분위기 맛집", "데이트하기 좋아요", "로맨틱해요", "특별한 날 오기 좋아요", "고급스러워요", "인테리어가 예뻐요", "사진 찍기 좋아요", "뷰가 좋아요");
            link(fd6, TagGroup.ETC, "친절해요", "주차하기 편해요", "위치가 좋아요");
        }
    }

    private void link(PlaceGroup placeGroup, TagGroup tagGroup, String... tagNames) {
        for (String tagName : tagNames) {
            TagValue tagValue = findOrCreateTagValue(tagName);
            Tag createdTag = Tag.builder()
                    .placeGroup(placeGroup)
                    .tagValue(tagValue)
                    .tagGroup(tagGroup)
                    .build();
            tagRepository.save(createdTag);
        }
    }

    private TagValue findOrCreateTagValue(String name) {
        TagValue tagValue = tagValueMap.get(name);
        if (tagValue != null) {
            return tagValue;
        }

        TagValue savedTag = tagValueRepository.findByName(name)
                .orElseGet(() -> tagValueRepository.save(
                        TagValue.builder().name(name).build()
                ));
        tagValueMap.put(name, savedTag);

        return savedTag;
    }
}