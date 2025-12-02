package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({TestSecurityConfiguration.class})
class CourseViewControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CourseCommentRepository courseCommentRepository;

    private final double GANGNAM_LAT = 37.4979;
    private final double GANGNAM_LON = 127.0276;

    private UsernamePasswordAuthenticationToken viewerAuthToken;

    private Member author;
    private Member viewer;
    private PlaceGroup placeGroup;
    private Course targetCourse;

    @BeforeEach
    void setup() {
        cleanUp();

        author = memberRepository.save(MemberEntityFixture.getMember());
        viewer = memberRepository.save(MemberEntityFixture.getMember("viewer"));
        placeGroup = placeGroupRepository.save(PlaceEntityFixture.getFoodPlaceGroup());

        CustomUserDetails userDetails = new CustomUserDetails(viewer, Collections.emptyMap());
        viewerAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        createSingleCourse();
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
    @DisplayName("100명의 사용자가 동시에 코스 상세 조회를 요청할 때, 조회수가 동시성 문제 없이 증가해야 한다")
    void performance_test_get_course_details_increase_view_count() throws InterruptedException {

        // given
        int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Long courseId = targetCourse.getId();
        long initialViewCount = targetCourse.getViewCount(); // 초기 조회수

        long startTime = System.currentTimeMillis();

        // when
        try {
            for (int i = 0; i < threads; i++) {
                executorService.execute(() -> {
                    try {
                        mockMvc.perform(get("/course/" + courseId)
                                        .with(authentication(viewerAuthToken))
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                        successCount.getAndIncrement();
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
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        long finalViewCount = updatedCourse.getViewCount();

        System.out.println("==========================");
        System.out.println("총 소요 시간: " + totalTime + "ms");
        System.out.println("평균 소요 시간: " + (double) totalTime / threads + "ms");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("초기 조회수: " + initialViewCount);
        System.out.println("최종 조회수: " + finalViewCount);
        System.out.println("==========================");

        assertThat(failCount.get()).isZero();
        assertThat(successCount.get()).isEqualTo(threads);
        // 동시성 검증
        assertThat(finalViewCount).as("조회수는 요청 횟수만큼 정확히 증가해야 합니다").isEqualTo(initialViewCount + threads);
    }

    private void createSingleCourse() {
        Place place = PlaceEntityFixture.getPlace(placeGroup, GANGNAM_LAT, GANGNAM_LON);
        placeRepository.save(place);

        targetCourse = CourseEntityFixture.getCourse(author);
        courseRepository.save(targetCourse);

        Pin pin = PinEntityFixture.getPin(targetCourse, place);
        pinRepository.save(pin);
    }
}