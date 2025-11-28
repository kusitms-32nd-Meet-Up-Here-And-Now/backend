package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.response.PlaceDetailsResponseDto;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseDetailsViewServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PlaceScrapRepository placeScrapRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private CourseDetailsViewService courseDetailsViewService;

    @Test
    @DisplayName("getCourseById는 코스가 존재하면 Optional<Course>를 반환한다")
    void get_course_by_id() {

        // given
        Long courseId = 1L;
        Course mockCourse = mock(Course.class);
        given(courseRepository.findCourseDetailsById(courseId)).willReturn(Optional.of(mockCourse));

        // when
        Optional<Course> result = courseDetailsViewService.getCourseById(courseId);

        // then
        assertThat(result).isPresent();
        assertThat(result).contains(mockCourse);
        verify(courseRepository).findCourseDetailsById(courseId);
    }

    @Test
    @DisplayName("getCourseById는 코스가 없으면 empty를 반환한다")
    void get_course_by_id_empty() {

        // given
        Long courseId = 99L;
        given(courseRepository.findCourseDetailsById(courseId)).willReturn(Optional.empty());

        // when
        Optional<Course> result = courseDetailsViewService.getCourseById(courseId);

        // then
        assertThat(result).isEmpty();
        verify(courseRepository).findCourseDetailsById(courseId);
    }

    @Test
    @DisplayName("getScrappedPlaceIds는 코스 내 핀들에 스크랩된 장소가 있으면 id set을 반환한다")
    void get_scrapped_place_ids_set() {

        // given
        Member mockMember = mock(Member.class);
        Course mockCourse = mock(Course.class);
        Place place = mock(Place.class);
        Pin pin = mock(Pin.class);

        given(mockCourse.getPinList()).willReturn(List.of(pin));
        given(pin.getPlace()).willReturn(place);
        given(placeScrapRepository.findScrappedPlaceIdsByMemberAndPlaces(mockMember, List.of(place)))
                .willReturn(Set.of(1L));

        // when
        Set<Long> result = courseDetailsViewService.getScrappedPlaceIds(mockMember, mockCourse);

        // then
        assertThat(result).isEqualTo(Set.of(1L));
        verify(placeScrapRepository).findScrappedPlaceIdsByMemberAndPlaces(mockMember, List.of(place));
    }

    @Test
    @DisplayName("getScrappedPlaceIds는 코스 내 핀이 없으면 빈 Set을 반환한다")
    void get_scrapped_place_ids_set_empty() {

        // given
        Member mockMember = mock(Member.class);
        Course mockCourse = mock(Course.class);
        given(mockCourse.getPinList()).willReturn(Collections.emptyList());

        // when
        Set<Long> result = courseDetailsViewService.getScrappedPlaceIds(mockMember, mockCourse);

        // then
        assertThat(result).isEmpty();
        verify(placeScrapRepository, never()).findScrappedPlaceIdsByMemberAndPlaces(any(), any());
    }

    @Test
    @DisplayName("toPinDetailsDto는 pin 정보와 place 정보를 DTO로 변환한다")
    void to_pin_details_dto() {

        // given
        Pin mockPin = mock(Pin.class);
        Place mockPlace = mock(Place.class);
        Point mockPoint = mock(Point.class);
        PinImage img1 = mock(PinImage.class);
        PinImage img2 = mock(PinImage.class);

        int index = 1;
        Long placeId = 10L;
        Set<Long> scrappedPlaceIds = Set.of(placeId);

        given(mockPlace.getId()).willReturn(placeId);
        given(mockPlace.getPlaceName()).willReturn("카페");
        given(mockPlace.getPlaceStreetNameAddress()).willReturn("강남");
        given(mockPlace.getLocation()).willReturn(mockPoint);
        given(mockPoint.getY()).willReturn(37.123);
        given(mockPoint.getX()).willReturn(127.456);
        given(mockPlace.getPlaceRating()).willReturn(BigDecimal.valueOf(4.5));
        given(mockPlace.getPinCount()).willReturn(5L);

        given(mockPin.getPlace()).willReturn(mockPlace);
        given(mockPin.getPinPositive()).willReturn("좋아요");
        given(mockPin.getPinNegative()).willReturn("아쉬워요");
        given(mockPin.getPinImages()).willReturn(List.of(img1, img2));

        given(img1.getImageUrl()).willReturn("image1.jpg");
        given(img2.getImageUrl()).willReturn("image2.jpg");
        given(objectStorageService.buildImageUrl("image1.jpg")).willReturn("http://image1.jpg");
        given(objectStorageService.buildImageUrl("image2.jpg")).willReturn("http://image2.jpg");

        // when
        PinDetailsResponseDto result = courseDetailsViewService.toPinDetailsDto(mockPin, index, scrappedPlaceIds);

        // then
        assertThat(result).isNotNull();
        assertThat(result.pinIndex()).isEqualTo(index);
        assertThat(result.pinPositive()).isEqualTo("좋아요");
        assertThat(result.pinNegative()).isEqualTo("아쉬워요");
        assertThat(result.pinImages()).containsExactly("http://image1.jpg", "http://image2.jpg");

        PlaceDetailsResponseDto placeDto = result.placeDetails();
        assertThat(placeDto).isNotNull();
        assertThat(placeDto.placeName()).isEqualTo("카페");
        assertThat(placeDto.placeStreetNameAddress()).isEqualTo("강남");
        assertThat(placeDto.placeLatitude()).isEqualTo(37.123);
        assertThat(placeDto.placeLongitude()).isEqualTo(127.456);
        assertThat(placeDto.scrapped()).isTrue(); // 스크랩됨
        assertThat(placeDto.placeRating()).isEqualTo(4.5);
        assertThat(placeDto.reviewCount()).isEqualTo(5);

        verify(objectStorageService, times(2)).buildImageUrl(anyString());
    }
}