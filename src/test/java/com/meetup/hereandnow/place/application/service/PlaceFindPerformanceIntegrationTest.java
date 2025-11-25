package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.TestContainerSupport;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PlaceFindPerformanceIntegrationTest extends TestContainerSupport {

    @Autowired
    private PlaceFindService placeFindService;

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;
    @Autowired
    private TagRepository tagRepository;

    // 강남역 좌표
    private final double GANGNAM_LAT = 37.4982667167977;
    private final double GANGNAM_LON = 127.026842105662;

    private PlaceGroup testPlaceGroup;

    @BeforeEach
    void setup() {
        cleanUp();
        testPlaceGroup = placeGroupRepository.save(PlaceEntityFixture.getFoodPlaceGroup());
        createBulkData(1000);
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        tagRepository.deleteAllInBatch();
        placeRepository.deleteAllInBatch();
        placeGroupRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 홈에서 리뷰순 장소 조회를 요청할 때 소요 시간을 측정한다")
    void performance_test_find_nearby_places() throws InterruptedException {

        // given
        int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Pageable pageable = SortUtils.resolvePlaceSortNQ(0, 20, SortType.SCRAPS);
        long startTime = System.currentTimeMillis();

        // when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    List<Place> result = placeFindService.findNearbyPlaces(
                            GANGNAM_LAT, GANGNAM_LON, pageable
                    );
                    if (!result.isEmpty()) {
                        successCount.getAndIncrement();
                    }
                } catch (Exception e) {
                    failCount.getAndIncrement();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
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
        for (int i = 0; i < count; i++) {
            Place place = PlaceEntityFixture.getPlace(testPlaceGroup, GANGNAM_LAT, GANGNAM_LON);
            places.add(place);
        }
        placeRepository.saveAll(places);
    }
}
