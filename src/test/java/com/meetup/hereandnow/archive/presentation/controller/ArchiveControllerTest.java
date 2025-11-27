package com.meetup.hereandnow.archive.presentation.controller;

import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(TestSecurityConfiguration.class)
class ArchiveControllerTest extends IntegrationTestSupport {

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
    private PinRepository pinRepository;
    @Autowired
    private CourseCommentRepository courseCommentRepository;
    @Autowired
    private TagRepository tagRepository;

    private Member member;
    private UsernamePasswordAuthenticationToken token;

    @BeforeEach
    void setup() {
        cleanUp();
        member = memberRepository.save(MemberEntityFixture.getMember());
        CustomUserDetails userDetails = new CustomUserDetails(member, Collections.emptyMap());
        token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
    @DisplayName("GET /archive/recent - 내가 만든 코스 중 가장 최근 코스 1개를 상세 조회한다")
    void get_recent_archive_success() throws Exception {

        // given
        courseRepository.save(CourseEntityFixture.getCourse(member));
        Course recentCourse = courseRepository.save(CourseEntityFixture.getCourse(member));

        // when & then
        mockMvc.perform(get("/archive/recent")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseId").value(recentCourse.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /archive/recent - 만든 코스가 없으면 data가 null이어야 한다")
    void get_recent_archive_empty() throws Exception {
        // given (코스 없음)
        // when, then
        mockMvc.perform(get("/archive/recent")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /archive/created - 내가 만든 코스 리스트를 페이징하여 조회한다")
    void get_my_created_courses_success() throws Exception {

        // given
        // 코스 5개 생성
        for (int i = 1; i <= 5; i++) {
            courseRepository.save(CourseEntityFixture.getCourse(member));
        }

        // when, then
        mockMvc.perform(get("/archive/created")
                        .param("page", "0")
                        .param("size", "3")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].courseTitle").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /archive/search - 필터 조건(지역)에 맞는 내 코스를 검색한다")
    void get_filtered_archive_courses_success() throws Exception {

        // given
        // 조건에 맞는 코스
        courseRepository.save(CourseEntityFixture.getCourse(member, "서울"));
        // 조건에 안 맞는 코스
        courseRepository.save(CourseEntityFixture.getCourse(member, "제주"));

        // when, then
        mockMvc.perform(get("/archive/search")
                        .param("region", "서울")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filteredCourses.length()").value(1))
                .andExpect(jsonPath("$.data.selectedFilters.region").value("서울"))
                .andDo(print());
    }

    @Test
    @DisplayName("GET /archive/search - 검색 결과가 없으면 빈 리스트를 반환한다")
    void get_filtered_archive_courses_empty() throws Exception {

        // given
        courseRepository.save(CourseEntityFixture.getCourse(member, "제주"));

        // when, then
        mockMvc.perform(get("/archive/search")
                        .param("region", "서울")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.filteredCourses").isEmpty())
                .andExpect(jsonPath("$.data.selectedFilters.region").value("서울"))
                .andDo(print());
    }
}