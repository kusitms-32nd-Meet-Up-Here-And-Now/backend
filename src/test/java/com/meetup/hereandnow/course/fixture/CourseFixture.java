package com.meetup.hereandnow.course.fixture;

import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.dto.request.PlaceSaveDto;
import java.time.LocalDate;
import java.util.List;

public class CourseFixture {

    public static PlaceSaveDto place() {
        return new PlaceSaveDto(
                "테스트 장소",
                "도로명 주소",
                "지번 주소",
                37.102,
                127.209,
                "FD6",
                "카페",
                "https://place.map.kakao.com/22105109"
        );
    }

    public static PinSaveDto pin() {
        return new PinSaveDto(
                4,
                "좋은 점",
                "나쁜 점",
                List.of("음식이 맛있어요", "주차하기 편해요"),
                place()
        );
    }

    public static CourseSaveDto course() {
        return new CourseSaveDto(
                "테스트 제목",
                "설명",
                "좋은 점",
                "나쁜 점",
                true,
                LocalDate.now(),
                "연인",
                "종로",
                5,
                List.of(pin())
        );
    }

}
