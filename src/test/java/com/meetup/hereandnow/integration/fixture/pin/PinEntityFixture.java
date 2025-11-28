package com.meetup.hereandnow.integration.fixture.pin;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.domain.Place;

import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import java.math.BigDecimal;

public class PinEntityFixture {

    public static Pin getPin(Course course, PlaceGroup placeGroup) {
        return getPin(course, PlaceEntityFixture.getPlace(placeGroup));
    }

    public static Pin getPin(Course course, Place place) {
        return Pin.builder()
                .pinRating(BigDecimal.valueOf(4.5))
                .pinPositive("분위기가 좋아요")
                .pinNegative("조금 시끄러워요")
                .course(course)
                .place(place)
                .build();
    }
}
