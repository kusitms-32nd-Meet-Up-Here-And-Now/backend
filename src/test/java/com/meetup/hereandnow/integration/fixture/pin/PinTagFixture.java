package com.meetup.hereandnow.integration.fixture.pin;

import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.entity.TagValue;
import com.meetup.hereandnow.tag.domain.value.TagGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PinTagFixture {

    private static final Random RANDOM = new Random();
    // TagInitializer에서 FD6(음식점)에 연결된 태그 이름들
    private static final List<String> FOOD_TAG_NAMES = Arrays.asList(
            "음식이 맛있어요", "메뉴가 다양해요", "특별한 메뉴가 있어요",
            "분위기 맛집", "데이트하기 좋아요", "로맨틱해요", "특별한 날 오기 좋아요",
            "고급스러워요", "인테리어가 예뻐요", "사진 찍기 좋아요", "뷰가 좋아요",
            "친절해요", "주차하기 편해요", "위치가 좋아요"
    );

    public static PinTag getPinTag(Pin pin, Tag tag) {
        return PinTag.builder()
                .tag(tag)
                .pin(pin)
                .build();
    }

    public static Tag getRandomFoodTag(PlaceGroup foodPlaceGroup, TagValue tagValue) {
        // TagInitializer에 의해 생성될 Tag를 가정
        return Tag.builder()
                .placeGroup(foodPlaceGroup)
                .tagValue(tagValue)
                .tagGroup(TagGroup.ATMOSPHERE) // 예시로 ATMOSPHERE 그룹 사용
                .build();
    }

    public static String getRandomFoodTagName() {
        return FOOD_TAG_NAMES.get(RANDOM.nextInt(FOOD_TAG_NAMES.size()));
    }
}
