package com.meetup.hereandnow.scrap.presentation.controller;

import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
class ScrapControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;
    @Autowired
    private CourseScrapRepository courseScrapRepository;
    @Autowired
    private PlaceScrapRepository placeScrapRepository;
    @Autowired
    private TagRepository tagRepository;

    private Member member;
    private UsernamePasswordAuthenticationToken token;
    private PlaceGroup placeGroup;

    @BeforeEach
    void setup() {
        cleanUp();
        member = memberRepository.save(MemberEntityFixture.getMember());
        placeGroup = placeGroupRepository.save(PlaceEntityFixture.getFoodPlaceGroup());
        CustomUserDetails userDetails = new CustomUserDetails(member, Collections.emptyMap());
        token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
    @DisplayName("POST /scrap/course/{id} - 코스 스크랩을 토글한다 (생성 -> 삭제)")
    void toggle_scrap_course() throws Exception {

        // given
        Course course = courseRepository.save(CourseEntityFixture.getCourse(member));

        // 스크랩 생성 (deleted == false)
        mockMvc.perform(post("/scrap/course/" + course.getId())
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(false))
                .andExpect(jsonPath("$.data.targetId").value(course.getId()))
                .andDo(print());

        // 스크랩 취소 (deleted == true)
        mockMvc.perform(post("/scrap/course/" + course.getId())
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /scrap/place/{id} - 장소 스크랩을 토글한다 (생성 -> 삭제)")
    void toggle_scrap_place() throws Exception {

        // given
        Place place = placeRepository.save(PlaceEntityFixture.getPlace(placeGroup));

        // 스크랩 생성
        mockMvc.perform(post("/scrap/place/" + place.getId())
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(false))
                .andExpect(jsonPath("$.data.targetId").value(place.getId()))
                .andDo(print());

        // 스크랩 취소
        mockMvc.perform(post("/scrap/place/" + place.getId())
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /scrap/course - 내가 스크랩한 코스 목록을 조회한다")
    void get_scrapped_courses() throws Exception {

        // given
        Course course1 = courseRepository.save(CourseEntityFixture.getCourse(member));
        Course course2 = courseRepository.save(CourseEntityFixture.getCourse(member));

        // 스크랩 데이터
        CourseScrap scrap1 = CourseScrap.builder().member(member).course(course1).build();
        CourseScrap scrap2 = CourseScrap.builder().member(member).course(course2).build();
        courseScrapRepository.save(scrap1);
        courseScrapRepository.save(scrap2);

        // when, then
        mockMvc.perform(get("/scrap/course")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /scrap/course - 스크랩한 코스가 없으면 빈 리스트를 반환한다")
    void get_scrapped_courses_empty() throws Exception {
        // given (스크랩 없음)
        // when, then
        mockMvc.perform(get("/scrap/course")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /scrap/place - 내가 스크랩한 장소 목록을 조회한다")
    void get_scrapped_places() throws Exception {

        // given
        Place place1 = placeRepository.save(PlaceEntityFixture.getPlace(placeGroup));
        Place place2 = placeRepository.save(PlaceEntityFixture.getPlace(placeGroup));

        // 스크랩 데이터
        PlaceScrap scrap1 = PlaceScrap.builder().member(member).place(place1).build();
        PlaceScrap scrap2 = PlaceScrap.builder().member(member).place(place2).build();
        placeScrapRepository.save(scrap1);
        placeScrapRepository.save(scrap2);

        // when, then
        mockMvc.perform(get("/scrap/place")
                        .param("page", "0")
                        .param("size", "10")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andDo(print());
    }
}