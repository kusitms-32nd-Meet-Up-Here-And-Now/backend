package com.meetup.hereandnow.place.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceInfoResponseDto;
import com.meetup.hereandnow.place.exception.PlaceErrorCode;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaceDetailService 테스트")
class PlaceDetailServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PinRepository pinRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private PlaceDetailService placeDetailService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Nested
    @DisplayName("getPlaceDetail 메서드는")
    class GetPlaceDetailTest {

        @Test
        @DisplayName("장소 상세 정보를 정상적으로 반환한다")
        void success_getPlaceDetail() {
            // given
            Long placeId = 1L;
            Place place = createTestPlace(placeId);
            Member member = createTestMember();
            Course course1 = createTestCourse(1L, member);
            Course course2 = createTestCourse(2L, member);

            Pin pin1 = createTestPin(1L, course1, place, "좋았던 점 1", "아쉬운 점 1");
            Pin pin2 = createTestPin(2L, course2, place, "좋았던 점 2", "아쉬운 점 2");
            Pin pin3 = createTestPin(3L, course1, place, "좋았던 점 1", "아쉬운 점 3");

            PinImage image1 = createTestPinImage(1L, pin1, "http://image1.jpg");
            PinImage image2 = createTestPinImage(2L, pin1, "http://image2.jpg");
            PinImage image3 = createTestPinImage(3L, pin2, "http://image3.jpg");

            pin1.getPinImages().add(image1);
            pin1.getPinImages().add(image2);
            pin2.getPinImages().add(image3);

            course1.getPinList().add(pin1);
            course1.getPinList().add(pin3);
            course2.getPinList().add(pin2);

            List<Pin> pinList = List.of(pin1, pin2, pin3);

            when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
            when(pinRepository.findAllByPlace(place)).thenReturn(pinList);

            // when
            PlaceInfoResponseDto result = placeDetailService.getPlaceDetail(placeId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.placeCardResponseDto()).isNotNull();
            assertThat(result.placeCardResponseDto().placeId()).isEqualTo(placeId);
            assertThat(result.placeCardResponseDto().placeName()).isEqualTo("테스트 장소");

            assertThat(result.placeTagList()).hasSize(2);
            assertThat(result.placeTagList()).contains("분위기가 좋아요", "사진 찍기 좋아요");

            assertThat(result.bannerImageList()).hasSize(3);
            assertThat(result.bannerImageList()).contains("http://image1.jpg", "http://image2.jpg", "http://image3.jpg");

            assertThat(result.placeInfoImageList()).hasSize(3);

            assertThat(result.placePositiveList()).hasSize(2);
            assertThat(result.placePositiveList()).contains("좋았던 점 1", "좋았던 점 2");

            assertThat(result.placeNegativeList()).hasSize(3);
            assertThat(result.placeNegativeList()).contains("아쉬운 점 1", "아쉬운 점 2", "아쉬운 점 3");

            assertThat(result.courseList()).hasSize(2);

            verify(placeRepository, times(1)).findById(placeId);
            verify(pinRepository, times(1)).findAllByPlace(place);
        }

        @Test
        @DisplayName("존재하지 않는 장소 ID로 조회 시 예외를 발생시킨다")
        void fail_getPlaceDetail_notFoundPlace() {
            // given
            Long placeId = 999L;
            when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> placeDetailService.getPlaceDetail(placeId))
                    .isInstanceOf(PlaceErrorCode.NOT_FOUND_PLACE.toException().getClass());

            verify(placeRepository, times(1)).findById(placeId);
            verify(pinRepository, never()).findAllByPlace(any());
        }

        @Test
        @DisplayName("Pin이 없는 장소의 상세 정보를 정상적으로 반환한다")
        void success_getPlaceDetail_noPins() {
            // given
            Long placeId = 1L;
            Place place = createTestPlace(placeId);
            List<Pin> emptyPinList = new ArrayList<>();

            when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
            when(pinRepository.findAllByPlace(place)).thenReturn(emptyPinList);

            // when
            PlaceInfoResponseDto result = placeDetailService.getPlaceDetail(placeId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.placeCardResponseDto()).isNotNull();
            assertThat(result.placeCardResponseDto().placeImageUrl()).isNull();
            assertThat(result.placeTagList()).hasSize(2); // Place에 저장된 태그
            assertThat(result.bannerImageList()).isEmpty();
            assertThat(result.placeInfoImageList()).isEmpty();
            assertThat(result.placePositiveList()).isEmpty();
            assertThat(result.placeNegativeList()).isEmpty();
            assertThat(result.courseList()).isEmpty();
        }

        @Test
        @DisplayName("좋았던 점/아쉬운 점이 null이거나 빈 문자열인 경우 필터링한다")
        void success_getPlaceDetail_filterNullAndBlank() {
            // given
            Long placeId = 1L;
            Place place = createTestPlace(placeId);
            Member member = createTestMember();
            Course course = createTestCourse(1L, member);

            Pin pin1 = createTestPin(1L, course, place, "좋았던 점", "아쉬운 점");
            Pin pin2 = createTestPin(2L, course, place, null, "");
            Pin pin3 = createTestPin(3L, course, place, "  ", null);

            course.getPinList().add(pin1);
            course.getPinList().add(pin2);
            course.getPinList().add(pin3);

            List<Pin> pinList = List.of(pin1, pin2, pin3);

            when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
            when(pinRepository.findAllByPlace(place)).thenReturn(pinList);

            // when
            PlaceInfoResponseDto result = placeDetailService.getPlaceDetail(placeId);

            // then
            assertThat(result.placePositiveList()).hasSize(1);
            assertThat(result.placePositiveList()).contains("좋았던 점");
            assertThat(result.placeNegativeList()).hasSize(1);
            assertThat(result.placeNegativeList()).contains("아쉬운 점");
        }

        @Test
        @DisplayName("중복된 좋았던 점/아쉬운 점을 제거한다")
        void success_getPlaceDetail_removeDuplicates() {
            // given
            Long placeId = 1L;
            Place place = createTestPlace(placeId);
            Member member = createTestMember();
            Course course = createTestCourse(1L, member);

            Pin pin1 = createTestPin(1L, course, place, "좋았던 점", "아쉬운 점");
            Pin pin2 = createTestPin(2L, course, place, "좋았던 점", "아쉬운 점");
            Pin pin3 = createTestPin(3L, course, place, "좋았던 점", "다른 아쉬운 점");

            course.getPinList().add(pin1);
            course.getPinList().add(pin2);
            course.getPinList().add(pin3);

            List<Pin> pinList = List.of(pin1, pin2, pin3);

            when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
            when(pinRepository.findAllByPlace(place)).thenReturn(pinList);

            // when
            PlaceInfoResponseDto result = placeDetailService.getPlaceDetail(placeId);

            // then
            assertThat(result.placePositiveList()).hasSize(1);
            assertThat(result.placePositiveList()).contains("좋았던 점");
            assertThat(result.placeNegativeList()).hasSize(2);
            assertThat(result.placeNegativeList()).contains("아쉬운 점", "다른 아쉬운 점");
        }
    }

    // Helper methods
    private Place createTestPlace(Long id) {
        Coordinate coord = new Coordinate(127.1, 37.1);
        Point point = geometryFactory.createPoint(coord);

        return Place.builder()
                .id(id)
                .placeName("테스트 장소")
                .placeStreetNameAddress("서울시 강남구 테스트로 123")
                .placeNumberAddress("테스트동 456-7")
                .location(point)
                .placeGroup(PlaceGroup.builder().id(1L).name("음식점").build())
                .placeCategory("카페")
                .placeRating(BigDecimal.valueOf(4.5))
                .placeTags(List.of("분위기가 좋아요", "사진 찍기 좋아요"))
                .pinCount(10L)
                .scrapCount(5L)
                .build();
    }

    private Member createTestMember() {
        return Member.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("테스트유저")
                .profileImage("http://profile.jpg")
                .providerId("provider123")
                .provider(Provider.KAKAO)
                .build();
    }

    private Course createTestCourse(Long id, Member member) {
        return Course.builder()
                .id(id)
                .courseTitle("테스트 코스 " + id)
                .courseDescription("테스트 설명")
                .courseRegion("강남")
                .courseVisitDate(LocalDate.now())
                .courseVisitMember("커플")
                .courseRating(BigDecimal.valueOf(4.0))
                .courseTags(List.of("맛집", "데이트"))
                .member(member)
                .pinList(new ArrayList<>())
                .isPublic(true)
                .build();
    }

    private Pin createTestPin(Long id, Course course, Place place, String positive, String negative) {
        return Pin.builder()
                .id(id)
                .course(course)
                .place(place)
                .pinPositive(positive)
                .pinNegative(negative)
                .pinRating(BigDecimal.valueOf(4.0))
                .pinImages(new ArrayList<>())
                .build();
    }

    private PinImage createTestPinImage(Long id, Pin pin, String imageUrl) {
        return PinImage.builder()
                .id(id)
                .pin(pin)
                .imageUrl(imageUrl)
                .build();
    }
}
