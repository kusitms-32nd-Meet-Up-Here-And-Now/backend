package com.meetup.hereandnow.connect.application.info;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseBannerResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoResponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.place.domain.Place;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleInfoSearchServiceTest {

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CoupleInfoSearchService coupleInfoSearchService;

    private Member member1;
    private Member member2;
    private Couple couple;
    private MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
                .id(1L)
                .nickname("김히어")
                .profileImage("http://profile1.jpg")
                .build();

        member2 = Member.builder()
                .id(2L)
                .nickname("이나우")
                .profileImage("http://profile2.jpg")
                .build();

        couple = Couple.builder()
                .id(1L)
                .member1(member1)
                .member2(member2)
                .coupleStartDate(LocalDate.of(2025, 1, 1))
                .build();

        mockedSecurity = mockStatic(SecurityUtils.class);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member1);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("커넥팅 - 커플 정보 조회 테스트")
    class CoupleInfoTest {
        @Test
        @DisplayName("커플 정보 조회 성공 - 코스와 핀이 있는 경우")
        void success_get_couple_info_course_with_pin() {
            // given
            List<Course> courseList = createCoursesWithPins(3, 4);

            when(coupleRepository.findByMember(member1)).thenReturn(Optional.of(couple));
            when(courseRepository.findByCourseVisitMemberAndMemberIn(eq("연인"), anyList()))
                    .thenReturn(courseList);

            // when
            CoupleInfoResponseDto result = coupleInfoSearchService.getCoupleInfoResponse();

            // then
            assertThat(result).isNotNull();
            assertThat(result.member1Name()).isEqualTo("김히어");
            assertThat(result.member2Name()).isEqualTo("이나우");
            assertThat(result.member1ImageUrl()).isEqualTo("http://profile1.jpg");
            assertThat(result.member2ImageUrl()).isEqualTo("http://profile2.jpg");
            assertThat(result.courseWithCount()).isEqualTo(3);
            assertThat(result.placeWithCount()).isEqualTo(12);
            assertThat(result.datingDate()).isGreaterThan(0);

            verify(coupleRepository).findByMember(member1);
            verify(courseRepository).findByCourseVisitMemberAndMemberIn(eq("연인"), anyList());
        }

        @Test
        @DisplayName("커플 정보 조회 성공 - 코스가 없는 경우")
        void success_get_couple_info_without_course() {
            // given
            when(coupleRepository.findByMember(member1)).thenReturn(Optional.of(couple));
            when(courseRepository.findByCourseVisitMemberAndMemberIn(eq("연인"), anyList()))
                    .thenReturn(List.of());

            // when
            CoupleInfoResponseDto result = coupleInfoSearchService.getCoupleInfoResponse();

            // then
            assertThat(result).isNotNull();
            assertThat(result.courseWithCount()).isZero();
            assertThat(result.placeWithCount()).isZero();
            verify(coupleRepository).findByMember(member1);
        }

        @Test
        @DisplayName("커플 정보 조회 실패 - 커플 정보를 찾을 수 없음")
        void fail_not_found_couple() {
            // given
            when(coupleRepository.findByMember(member1)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> coupleInfoSearchService.getCoupleInfoResponse())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());

            verify(coupleRepository).findByMember(member1);
            verify(courseRepository, never()).findByCourseVisitMemberAndMemberIn(any(), any());
        }
    }

    @Nested
    @DisplayName("커넥팅 - 메인 배너 조회 테스트")
    class GetBannerInfoTest {

        @Test
        @DisplayName("배너 조회 성공 - 페이징 처리")
        void success_get_banner_info_with_slice() {
            // given
            int page = 0;
            int size = 2;
            List<Course> courseList = createCoursesWithPins(3, 2);

            when(coupleRepository.findByMember(member1)).thenReturn(Optional.of(couple));
            when(courseRepository.findByCourseVisitMemberAndMemberIn(eq("연인"), anyList()))
                    .thenReturn(courseList);

            // when
            Slice<CoupleCourseBannerResponseDto> result =
                    coupleInfoSearchService.getBannerResponse(page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().getFirst().courseTitle()).isEqualTo("코스 제목 0");
            assertThat(result.getContent().getFirst().placeCount()).isEqualTo(2);
            assertThat(result.hasNext()).isTrue();

            verify(coupleRepository).findByMember(member1);
            verify(courseRepository).findByCourseVisitMemberAndMemberIn(eq("연인"), anyList());
        }

        @Test
        @DisplayName("배너 조회 성공 - 코스가 없는 경우")
        void success_get_banner_has_not_course() {
            // given
            int page = 0;
            int size = 10;

            when(coupleRepository.findByMember(member1)).thenReturn(Optional.of(couple));
            when(courseRepository.findByCourseVisitMemberAndMemberIn(eq("연인"), anyList()))
                    .thenReturn(List.of());

            // when
            Slice<CoupleCourseBannerResponseDto> result =
                    coupleInfoSearchService.getBannerResponse(page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.hasNext()).isFalse();

            verify(coupleRepository).findByMember(member1);
        }

        @Test
        @DisplayName("배너 조회 실패 - 커플 정보를 찾을 수 없음")
        void fail_get_banner_not_found_couple() {
            // given
            when(coupleRepository.findByMember(member1)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> coupleInfoSearchService.getBannerResponse(0, 10))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());

            verify(coupleRepository).findByMember(member1);
            verify(courseRepository, never()).findByCourseVisitMemberAndMemberIn(any(), any());
        }

        @Test
        @DisplayName("배너 조회 성공 - 마지막 페이지 확인")
        void success_get_banner_check_last_page() {
            // given
            int page = 0;
            int size = 5;
            List<Course> courseList = createCoursesWithPins(3, 2);

            when(coupleRepository.findByMember(member1)).thenReturn(Optional.of(couple));
            when(courseRepository.findByCourseVisitMemberAndMemberIn(eq("연인"), anyList()))
                    .thenReturn(courseList);

            // when
            Slice<CoupleCourseBannerResponseDto> result =
                    coupleInfoSearchService.getBannerResponse(page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.hasNext()).isFalse();

            verify(coupleRepository).findByMember(member1);
        }

    }

    private List<Course> createCoursesWithPins(int courseCount, int pinCountPerCourse) {
        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < courseCount; i++) {
            List<Pin> pinList = new ArrayList<>();

            for (int j = 0; j < pinCountPerCourse; j++) {
                Place place = Place.builder()
                        .id((long) (i * pinCountPerCourse + j))
                        .placeName("장소 " + j)
                        .build();

                PinImage pinImage = PinImage.builder()
                        .id((long) (i * pinCountPerCourse + j))
                        .imageUrl("http://image" + j + ".jpg")
                        .build();

                Pin pin = Pin.builder()
                        .id((long) (i * pinCountPerCourse + j))
                        .place(place)
                        .pinPositive("좋았어요")
                        .pinNegative("별로였어요")
                        .pinRating(BigDecimal.valueOf(4.5))
                        .pinImages(List.of(pinImage))
                        .build();

                pinList.add(pin);
            }

            Course course = Course.builder()
                    .id((long) i)
                    .member(member1)
                    .courseVisitDate(LocalDate.of(2025, 11, i + 1))
                    .courseVisitMember("연인")
                    .courseRegion("서울")
                    .courseTitle("코스 제목 " + i)
                    .courseDescription("코스 설명 " + i)
                    .courseRating(BigDecimal.valueOf(4.5))
                    .pinList(pinList)
                    .build();

            courseList.add(course);
        }

        return courseList;
    }
}