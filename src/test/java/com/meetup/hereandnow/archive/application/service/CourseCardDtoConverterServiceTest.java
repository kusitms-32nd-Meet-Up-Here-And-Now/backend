package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class CourseCardDtoConverterServiceTest {

    @InjectMocks
    private CourseCardDtoConverterService converterService;

    private Course course1, course2;

    @BeforeEach
    void setUp() {
        Member mockMember = mock(Member.class);

        PinImage img1 = PinImage.builder().id(1L).imageUrl("url1").build();
        PinImage img2 = PinImage.builder().id(2L).imageUrl("url2").build();
        PinImage img3 = PinImage.builder().id(3L).imageUrl("url3").build();

        Pin pin1 = Pin.builder()
                .pinImages(List.of(img2, img1))
                .build();
        Pin pin2 = Pin.builder()
                .pinImages(List.of(img3))
                .build();
        Pin pin3 = Pin.builder()
                .pinImages(Collections.emptyList())
                .build();

        course1 = Course.builder()
                .id(1L)
                .courseTitle("코스 1")
                .courseDescription("설명 1")
                .viewCount(100)
                .courseRating(BigDecimal.valueOf(4.5))
                .pinList(List.of(pin1, pin2))
                .courseTags(List.of("tag1", "tag2"))
                .member(mockMember)
                .build();

        course2 = Course.builder()
                .id(2L)
                .courseTitle("코스 2")
                .courseDescription("설명 2")
                .viewCount(200)
                .courseRating(BigDecimal.valueOf(3.0))
                .pinList(List.of(pin3))
                .courseTags(List.of("tag3", "tag4"))
                .member(mockMember)
                .build();
    }

    @Test
    @DisplayName("DTO 변환 시 코스, 태그, 핀의 첫번째 이미지 URL을 매핑해 반환한다")
    void convert_to_course_card_dto() {
        // given
        List<Course> courses = List.of(course1, course2);

        // when
        List<CourseCardDto> resultList = converterService.convertToCourseCardDto(courses);

        // then
        assertThat(resultList).hasSize(2);

        CourseCardDto dto1 = resultList.getFirst();
        assertThat(dto1.id()).isEqualTo(1L);
        assertThat(dto1.courseTitle()).isEqualTo("코스 1");
        assertThat(dto1.courseDescription()).isEqualTo("설명 1");
        assertThat(dto1.viewCount()).isEqualTo(100);
        assertThat(dto1.courseRating()).isEqualTo(4.5);
        assertThat(dto1.imageUrl()).containsExactly("url1", "url3");
        assertThat(dto1.courseTagList()).containsExactly("tag1", "tag2");

        CourseCardDto dto2 = resultList.get(1);
        assertThat(dto2.id()).isEqualTo(2L);
        assertThat(dto2.courseTitle()).isEqualTo("코스 2");
        assertThat(dto2.viewCount()).isEqualTo(200);
        assertThat(dto2.courseRating()).isEqualTo(3.0);
        assertThat(dto2.imageUrl()).isEmpty();
        assertThat(dto2.courseTagList()).containsExactly("tag3", "tag4");
    }

    @Test
    @DisplayName("DTO 변환 시 코스 리스트가 비어있으면 빈 리스트를 반환한다")
    void convert_to_course_card_dto_empty_course_list() {
        // given
        List<Course> courses = Collections.emptyList();

        // when
        List<CourseCardDto> resultList = converterService.convertToCourseCardDto(courses);

        // then
        assertThat(resultList).isEmpty();
    }
}
