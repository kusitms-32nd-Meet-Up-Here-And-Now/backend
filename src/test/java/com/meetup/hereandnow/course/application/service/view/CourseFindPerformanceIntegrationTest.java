package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.course.CourseCommentFixture;
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.fixture.pin.PinEntityFixture;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CourseFindPerformanceIntegrationTest extends IntegrationTestSupport {

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
    @Autowired
    private TagRepository tagRepository;

    private final double GANGNAM_LAT = 37.4982667167977;
    private final double GANGNAM_LON = 127.026842105662;

    private Member testMember;
    private PlaceGroup testPlaceGroup;

    @BeforeEach
    void setup() {
        cleanUp();
        testMember = memberRepository.save(MemberEntityFixture.getMember());
        testPlaceGroup = placeGroupRepository.save(PlaceEntityFixture.getFoodPlaceGroup());
        createBulkData(1000);
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        courseCommentRepository.deleteAllInBatch();
        pinRepository.deleteAllInBatch();
        tagRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        placeRepository.deleteAllInBatch();
        placeGroupRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 홈에서 리뷰순 코스 조회를 요청할 때 성공해야 하며, 소요 시간을 측정한다")
    void performance_test_get_nearby_courses_sorted_by_reviews() throws InterruptedException {

        // given
        int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        // when
        try {
            for (int i = 0; i < threads; i++) {
                executorService.execute(() -> {
                    try {
                        List<Course> result = courseFindService.getNearbyCourses(
                                0, 20, SortType.REVIEWS, GANGNAM_LAT, GANGNAM_LON
                        );
                        if (!result.isEmpty()) {
                            successCount.getAndIncrement();
                        }
                    } catch (Exception e) {
                        failCount.getAndIncrement();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            boolean completed = latch.await(60, TimeUnit.SECONDS);
            assertThat(completed).as("스레드가 시간 내에 모두 종료돼야 합니다.").isTrue();
        } finally {
            executorService.shutdown();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // then
        System.out.println("==========================");
        System.out.println("총 소요 시간: " + totalTime + "ms");
        System.out.println("평균 소요 시간: " + (double) totalTime / threads + "ms");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("==========================");

        assertThat(failCount.get()).isZero();
        assertThat(successCount.get()).as("모든 요청이 데이터를 찾았어야 합니다").isEqualTo(threads);
    }

    private void createBulkData(int count) {
        List<Place> places = new ArrayList<>();
        List<Course> courses = new ArrayList<>();
        List<Pin> pins = new ArrayList<>();
        List<CourseComment> comments = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Place place = PlaceEntityFixture.getPlace(testPlaceGroup, GANGNAM_LAT, GANGNAM_LON);
            Course course = CourseEntityFixture.getCourse(testMember);
            places.add(place);
            courses.add(course);
        }
        placeRepository.saveAll(places);
        courseRepository.saveAll(courses);

        for (int i = 0; i < count; i++) {
            Pin pin = PinEntityFixture.getPin(courses.get(i), places.get(i));
            pins.add(pin);
            if (i % 2 == 0) {
                CourseComment courseComment = CourseCommentFixture.getCourseComment(courses.get(i), testMember);
                comments.add(courseComment);
            }
        }
        pinRepository.saveAll(pins);
        courseCommentRepository.saveAll(comments);
    }
}