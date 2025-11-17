package com.meetup.hereandnow.integration.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.course.application.service.view.CourseFindService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
import com.meetup.hereandnow.member.repository.MemberRepository;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CourseFindServiceIT extends IntegrationTestSupport {

    @Autowired
    private CourseFindService courseFindService;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private CourseCommentRepository courseCommentRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private Member testMember;
    private PlaceGroup testPlaceGroup;

    // 강남역 좌표
    private final double GANGNAM_LAT = 37.4979;
    private final double GANGNAM_LON = 127.0276;
    private final Point GANGNAM_POINT = createPoint(GANGNAM_LON, GANGNAM_LAT);

    // 역삼역 좌표
    private final Point YEOKSAM_POINT = createPoint(127.0364, 37.5006);

    // 광화문 좌표
    private final Point GWANGHWAMUN_POINT = createPoint(126.9780, 37.5665);


    @BeforeEach
    void initializeTestData() {
        courseCommentRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        testMember = memberRepository.save(
                Member.builder()
                        .nickname("testUser")
                        .email("email")
                        .provider(Provider.GOOGLE)
                        .providerId("123")
                        .build()
        );
        testPlaceGroup = placeGroupRepository.save(
                PlaceGroup.builder()
                        .name("업종")
                        .code("CODE").build()
        );
    }

    @Test
    @DisplayName("getNearbyCourses(리뷰순): 코스들을 댓글 수 내림차순으로 정렬하여 반환한다")
    void get_nearby_courses_sorted_by_reviews() {

        // given
        Course courseGangnam = createCourse(testMember, GANGNAM_POINT, "강남 코스");
        Course courseYeoksam = createCourse(testMember, YEOKSAM_POINT, "역삼 코스");
        Course courseGwang = createCourse(testMember, GWANGHWAMUN_POINT, "광화문 코스");
        courseRepository.saveAll(List.of(courseGangnam, courseYeoksam, courseGwang));

        createComment(testMember, courseYeoksam);
        createComment(testMember, courseYeoksam);
        createComment(testMember, courseGangnam);
        createComment(testMember, courseGwang);
        createComment(testMember, courseGwang);
        createComment(testMember, courseGwang);

        // when (강남역 좌표 기준, 리뷰순)
        List<Course> result = courseFindService.getNearbyCourses(
                0, 5, SortType.REVIEWS, GANGNAM_LAT, GANGNAM_LON
        );

        // then
        assertThat(result).hasSize(2); // 광화문 코스 제외
        assertThat(result).extracting(Course::getId).containsExactly(courseYeoksam.getId(), courseGangnam.getId());
    }

    @Test
    @DisplayName("getNearbyCourses(최신순): 코스들을 createdAt 내림차순으로 정렬하여 반환한다")
    void get_nearby_courses_sorted_by_recent() {

        // given
        Course courseGangnam = createCourse(testMember, GANGNAM_POINT, "강남 코스");
        courseRepository.save(courseGangnam);
        Course courseGwang = createCourse(testMember, GWANGHWAMUN_POINT, "광화문 코스");
        courseRepository.save(courseGwang);
        Course courseYeoksam = createCourse(testMember, YEOKSAM_POINT, "역삼 코스");
        courseRepository.save(courseYeoksam);

        // when (강남역 좌표 기준, 최신순)
        List<Course> result = courseFindService.getNearbyCourses(
                0, 5, SortType.RECENT, GANGNAM_LAT, GANGNAM_LON
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Course::getId).containsExactly(courseYeoksam.getId(), courseGangnam.getId());
    }

    @Test
    @DisplayName("getNearbyCourses: 근처 코스가 없으면 빈 리스트를 반환한다")
    void get_nearby_courses_returns_empty_list_when_no_courses_nearby() {

        // given
        Course courseGwang = createCourse(testMember, GWANGHWAMUN_POINT, "광화문 코스");
        courseRepository.save(courseGwang);

        // when (강남역 좌표 기준, 최신순)
        List<Course> result = courseFindService.getNearbyCourses(
                0, 5, SortType.RECENT, GANGNAM_LAT, GANGNAM_LON
        );

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getCourses: 페이지네이션에 맞게 코스 목록을 반환한다")
    void get_courses_returns_content_from_page() {

        // given
        courseRepository.saveAll(List.of(
                createCourse(testMember, GANGNAM_POINT, "A"),
                createCourse(testMember, YEOKSAM_POINT, "B"),
                createCourse(testMember, GWANGHWAMUN_POINT, "C")
        ));

        Pageable pageable = PageRequest.of(0, 2);

        // when
        List<Course> result = courseFindService.getCourses(pageable);

        // then
        assertThat(result).hasSize(2);
    }

    /*
    좌표 생성 메서드
     */
    private Point createPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    /*
    코스 생성 메서드 (장소, 핀 의존성 포함)
     */
    private Course createCourse(Member member, Point location, String title) {

        Place place = Place.builder()
                .placeName("place")
                .placeStreetNameAddress("address")
                .location(location)
                .placeGroup(testPlaceGroup)
                .scrapCount(0L)
                .build();
        placeRepository.save(place);

        Course course = Course.builder()
                .courseVisitDate(LocalDate.of(2025, Month.JANUARY, 1))
                .courseVisitMember("연인")
                .courseRegion("region")
                .courseTitle(title)
                .courseDescription("설명")
                .isPublic(true)
                .viewCount(1)
                .scrapCount(1)
                .member(member)
                .build();
        courseRepository.save(course);

        Pin pin = Pin.builder()
                .pinPositive("좋아요")
                .pinNegative("별로예요")
                .place(place)
                .course(course)
                .build();
        pinRepository.save(pin);

        return course;
    }

    /*
    코스 댓글 생성 메서드
     */
    private void createComment(Member member, Course course) {
        CourseComment comment = CourseComment.builder()
                .member(member)
                .course(course)
                .content("...")
                .build();
        courseCommentRepository.save(comment);
    }
}