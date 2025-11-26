package com.meetup.hereandnow.place.presentation.controller;

import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PlaceHomeControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;
    @Autowired
    private TagRepository tagRepository;

    private final double TARGET_LAT = 37.5665;
    private final double TARGET_LON = 126.9782;

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
    @DisplayName("100명의 사용자가 동시에 홈에서 리뷰순 장소 조회를 요청할 때 성공해야 하며, 소요 시간을 측정한다")
    void performance_test_get_recommended_places_sorted_by_reviews() throws InterruptedException {

        // given
        int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        // when
        for (int i = 0; i < threads; i++) {
            executorService.execute(() -> {
                try {
                    mockMvc.perform(get("/place/home/recommended")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .param("sort", SortType.REVIEWS.name())
                                    .param("lat", String.valueOf(TARGET_LAT))
                                    .param("lon", String.valueOf(TARGET_LON))
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.data").isArray())
                            .andExpect(jsonPath("$.data.length()").value(20));

                    successCount.getAndIncrement();
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
        assertThat(successCount.get()).as("모든 요청이 성공해야 합니다").isEqualTo(threads);
    }

    private void createBulkData(int count) {
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Place place = PlaceEntityFixture.getPlace(testPlaceGroup, TARGET_LAT, TARGET_LON);
            places.add(place);
        }
        placeRepository.saveAll(places);
    }
}