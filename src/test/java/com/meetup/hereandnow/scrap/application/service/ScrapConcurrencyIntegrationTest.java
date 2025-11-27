package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.TestContainerSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScrapConcurrencyIntegrationTest extends TestContainerSupport {

    @Autowired
    private PlaceScrapService placeScrapService;
    @Autowired
    private CourseScrapService courseScrapService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;
    @Autowired
    private PlaceScrapRepository placeScrapRepository;
    @Autowired
    private CourseScrapRepository courseScrapRepository;

    private List<Member> members;
    private Place targetPlace;
    private Course targetCourse;
    private final int THREAD_COUNT = 100;

    @BeforeEach
    void setup() {
        cleanUp();

        // 사용자 생성
        members = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Member member = MemberEntityFixture.getMember("email" + i);
            members.add(member);
        }
        memberRepository.saveAll(members);

        // 테스트 대상 Place 생성
        PlaceGroup placeGroup = placeGroupRepository.save(PlaceEntityFixture.getFoodPlaceGroup());
        Place place = PlaceEntityFixture.getPlace(placeGroup);
        targetPlace = placeRepository.save(place);

        // 테스트 대상 Course 생성
        Course course = CourseEntityFixture.getCourse(members.getFirst());
        targetCourse = courseRepository.save(course);
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        tagRepository.deleteAllInBatch();
        courseScrapRepository.deleteAllInBatch();
        placeScrapRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        placeRepository.deleteAllInBatch();
        placeGroupRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Place 스크랩 동시성 테스트: 100명이 동시에 장소를 스크랩하면 scrapCount가 100 증가해야 한다")
    void concurrency_test_place_scrap_increase() throws InterruptedException {

        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long initialCount = targetPlace.getScrapCount();

        // when
        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int index = i;
                executorService.execute(() -> {
                    try {
                        // 각기 다른 사용자가 동일한 장소를 스크랩
                        placeScrapService.toggleScrapPlace(members.get(index), targetPlace.getId());
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

        // then
        Place updatedPlace = placeRepository.findById(targetPlace.getId()).orElseThrow();
        Long finalScrapCount = updatedPlace.getScrapCount();

        System.out.println("=== 장소 스크랩 동시성 테스트 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("초기값: " + initialCount);
        System.out.println("결과값: " + finalScrapCount);
        System.out.println("===========================");

        assertThat(failCount.get()).isZero();
        assertThat(finalScrapCount).isEqualTo(initialCount + THREAD_COUNT);
    }

    @Test
    @DisplayName("Course 스크랩 동시성 테스트: 100명이 동시에 코스를 스크랩하면 scrapCount가 100 증가해야 한다")
    void concurrency_test_course_scrap_increase() throws InterruptedException {

        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        int initialCount = targetCourse.getScrapCount();

        // when
        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int index = i;
                executorService.execute(() -> {
                    try {
                        // 각기 다른 사용자가 동일한 코스를 스크랩
                        courseScrapService.toggleScrapCourse(members.get(index), targetCourse.getId());
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

        // then
        Course updatedCourse = courseRepository.findById(targetCourse.getId()).orElseThrow();
        Integer finalScrapCount = updatedCourse.getScrapCount();

        System.out.println("=== 코스 스크랩 동시성 테스트 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());
        System.out.println("초기값: " + initialCount);
        System.out.println("결과값: " + finalScrapCount);
        System.out.println("===========================");

        assertThat(failCount.get()).isZero();
        assertThat(finalScrapCount).isEqualTo(initialCount + THREAD_COUNT);
    }
}