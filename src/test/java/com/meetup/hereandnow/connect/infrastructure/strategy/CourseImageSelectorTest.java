package com.meetup.hereandnow.connect.infrastructure.strategy;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.place.domain.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseImageSelector 테스트")
class CourseImageSelectorTest {

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private CourseImageSelector courseImageSelector;

    private Member member;
    private Place place;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .build();

        place = Place.builder()
                .id(1L)
                .placeName("테스트 장소")
                .build();
    }

    @Test
    @DisplayName("코스에 이미지가 없으면 빈 리스트를 반환한다")
    void selectRandomImages_WhenNoImages_ReturnsEmptyList() {
        // given
        Course course = Course.builder()
                .id(1L)
                .member(member)
                .courseVisitDate(LocalDate.now())
                .courseVisitMember("테스트 유저")
                .courseRegion("서울")
                .courseDescription("테스트 코스")
                .pinList(new ArrayList<>())
                .build();

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).isEmpty();
        verify(objectStorageService, times(0)).buildImageUrl(anyString());
    }

    @Test
    @DisplayName("코스에 이미지가 3개 미만이면 모든 이미지를 반환한다")
    void selectRandomImages_WhenLessThan3Images_ReturnsAllImages() {
        // given
        Course course = createCourseWithPins(2, 1); // 2개의 핀, 각 핀에 1개의 이미지

        given(objectStorageService.buildImageUrl(anyString()))
                .willAnswer(invocation -> "https://example.com/" + invocation.getArgument(0));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(2);
        verify(objectStorageService, times(2)).buildImageUrl(anyString());
    }

    @Test
    @DisplayName("코스에 이미지가 3개 이상이면 최대 3개만 반환한다")
    void selectRandomImages_WhenMoreThan3Images_Returns3Images() {
        // given
        Course course = createCourseWithPins(3, 2); // 3개의 핀, 각 핀에 2개의 이미지 (총 6개)

        given(objectStorageService.buildImageUrl(anyString()))
                .willAnswer(invocation -> "https://example.com/" + invocation.getArgument(0));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(3);
        verify(objectStorageService, times(3)).buildImageUrl(anyString());
    }

    @Test
    @DisplayName("코스에 이미지가 정확히 3개면 3개를 반환한다")
    void selectRandomImages_WhenExactly3Images_Returns3Images() {
        // given
        Course course = createCourseWithPins(3, 1); // 3개의 핀, 각 핀에 1개의 이미지

        given(objectStorageService.buildImageUrl(anyString()))
                .willAnswer(invocation -> "https://example.com/" + invocation.getArgument(0));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(3);
        verify(objectStorageService, times(3)).buildImageUrl(anyString());
    }

    @Test
    @DisplayName("핀 리스트가 비어있어도 예외 없이 빈 리스트를 반환한다")
    void selectRandomImages_WhenEmptyPinList_ReturnsEmptyList() {
        // given
        Course course = Course.builder()
                .id(1L)
                .member(member)
                .courseVisitDate(LocalDate.now())
                .courseVisitMember("테스트 유저")
                .courseRegion("서울")
                .courseDescription("테스트 코스")
                .pinList(new ArrayList<>())
                .build();

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("핀에 이미지가 없는 경우 빈 리스트를 반환한다")
    void selectRandomImages_WhenPinsHaveNoImages_ReturnsEmptyList() {
        // given
        Course course = Course.builder()
                .id(1L)
                .member(member)
                .courseVisitDate(LocalDate.now())
                .courseVisitMember("테스트 유저")
                .courseRegion("서울")
                .courseDescription("테스트 코스")
                .pinList(new ArrayList<>())
                .build();

        Pin pin = Pin.builder()
                .id(1L)
                .course(course)
                .place(place)
                .pinRating(BigDecimal.valueOf(4.0))
                .pinPositive("좋아요")
                .pinNegative("나빠요")
                .pinImages(new ArrayList<>()) // 이미지 없음
                .build();

        course.addPin(pin);

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("여러 핀의 이미지를 합쳐서 처리한다")
    void selectRandomImages_CombinesImagesFromMultiplePins() {
        // given
        Course course = createCourseWithPins(5, 2); // 5개의 핀, 각 핀에 2개의 이미지 (총 10개)

        given(objectStorageService.buildImageUrl(anyString()))
                .willAnswer(invocation -> "https://example.com/" + invocation.getArgument(0));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(3); // 최대 3개만 반환
        assertThat(result).allMatch(url -> url.startsWith("https://example.com/"));
    }

    private Course createCourseWithPins(int pinCount, int imageCountPerPin) {
        Course course = Course.builder()
                .id(1L)
                .member(member)
                .courseVisitDate(LocalDate.now())
                .courseVisitMember("테스트 유저")
                .courseRegion("서울")
                .courseDescription("테스트 코스")
                .pinList(new ArrayList<>())
                .build();

        for (int i = 0; i < pinCount; i++) {
            Pin pin = Pin.builder()
                    .id((long) (i + 1))
                    .course(course)
                    .place(place)
                    .pinRating(BigDecimal.valueOf(4.0))
                    .pinPositive("좋아요 " + i)
                    .pinNegative("나빠요 " + i)
                    .pinImages(new ArrayList<>())
                    .build();

            for (int j = 0; j < imageCountPerPin; j++) {
                PinImage pinImage = PinImage.builder()
                        .id((long) (i * imageCountPerPin + j + 1))
                        .imageUrl("course/" + course.getId() + "/pin_" + i + "_image_" + j + ".jpg")
                        .pin(pin)
                        .build();
                pin.addPinImage(pinImage);
            }

            course.addPin(pin);
        }

        return course;
    }
}

