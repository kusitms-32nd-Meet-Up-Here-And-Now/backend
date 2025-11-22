package com.meetup.hereandnow.tag.domain.value;

import lombok.Getter;

@Getter
public enum TagGroup {

    ATMOSPHERE("분위기"),
    FACILITY("시설"),
    FOOD_PRICE("음식/가격"),
    ETC("기타");

    private final String description;

    TagGroup(String description) {
        this.description = description;
    }
}
