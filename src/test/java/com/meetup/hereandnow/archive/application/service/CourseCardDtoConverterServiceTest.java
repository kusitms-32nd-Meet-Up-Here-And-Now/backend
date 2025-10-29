package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseTag;
import com.meetup.hereandnow.course.domain.value.CourseTagEnum;
import com.meetup.hereandnow.course.infrastructure.repository.CourseTagRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
class CourseCardDtoConverterServiceTest {

    @Mock
    private CourseTagRepository courseTagRepository;

    @InjectMocks
    private CourseCardDtoConverterService converterService;

    private Course course1, course2;
    private CourseTag ct1, ct2, ct3;
    private CourseTagEnum tagA, tagB, tagC;

    @BeforeEach
    void setUp() {
        Member mockMember = mock(Member.class);

        tagA = mock(CourseTagEnum.class);
        tagB = mock(CourseTagEnum.class);
        tagC = mock(CourseTagEnum.class);

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
                .member(mockMember)
                .build();

        course2 = Course.builder()
                .id(2L)
                .courseTitle("코스 2")
                .courseDescription("설명 2")
                .viewCount(200)
                .courseRating(BigDecimal.valueOf(3.0))
                .pinList(List.of(pin3))
                .member(mockMember)
                .build();

        ct1 = CourseTag.builder()
                .course(course1)
                .courseTagName(tagA)
                .build();
        ct2 = CourseTag.builder()
                .course(course1)
                .courseTagName(tagB)
                .build();
        ct3 = CourseTag.builder()
                .course(course2)
                .courseTagName(tagC)
                .build();
    }

    @Test
    @DisplayName("DTO 변환 시 코스, 태그, 핀의 첫번째 이미지 URL을 매핑해 반환한다")
    void convert_to_course_card_dto() {
        // given
        given(tagA.getName()).willReturn("태그A");
        given(tagB.getName()).willReturn("태그B");
        given(tagC.getName()).willReturn("태그C");

        List<Course> courses = List.of(course1, course2);
        List<Long> courseIds = List.of(1L, 2L);

        given(courseTagRepository.findAllByCourseIdIn(courseIds)).willReturn(List.of(ct1, ct2, ct3));

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
        assertThat(dto1.courseTagList()).containsExactlyInAnyOrder("태그A", "태그B");
        assertThat(dto1.imageUrl()).containsExactly("url1", "url3");

        CourseCardDto dto2 = resultList.get(1);
        assertThat(dto2.id()).isEqualTo(2L);
        assertThat(dto2.courseTitle()).isEqualTo("코스 2");
        assertThat(dto2.viewCount()).isEqualTo(200);
        assertThat(dto2.courseRating()).isEqualTo(3.0);
        assertThat(dto2.courseTagList()).containsExactly("태그C");
        assertThat(dto2.imageUrl()).isEmpty();
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
        then(courseTagRepository).should(never()).findAllByCourseIdIn(any());
    }
}
