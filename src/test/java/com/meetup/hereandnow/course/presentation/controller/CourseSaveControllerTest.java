package com.meetup.hereandnow.course.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.course.application.facade.CourseSaveFacade;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.integration.fixture.course.CourseSaveDtoFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfiguration.class)
class CourseSaveControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseSaveFacade courseSaveFacade;

    private static final String TEST_UUID = "uuid";
    private static final String TEST_DIRNAME = "/test/pin/uuid";

    @Test
    @DisplayName("POST /course/save")
    void saveCourse() throws Exception {
        // given
        CourseSaveDto courseSaveDto = CourseSaveDtoFixture.course();
        CourseSaveResponseDto courseSaveResponseDto = new CourseSaveResponseDto(
                TEST_UUID,
                List.of(new PinDirnameDto(
                        0, TEST_DIRNAME
                ))
        );

        given(courseSaveFacade.prepareCourseSave(courseSaveDto)).willReturn(courseSaveResponseDto);

        // when & then
        mockMvc.perform(post("/course/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseSaveDto)
                        )).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseKey").value(TEST_UUID));

    }

    @Test
    @DisplayName("POST /course/{courseUuid}/commit")
    void commitCourse() throws Exception {
        // given
        CommitSaveCourseRequestDto commitSaveCourseRequestDto = new CommitSaveCourseRequestDto(
                List.of(new PinImageObjectKeyDto(
                        0, List.of(TEST_DIRNAME)
                ))
        );

        CommitSaveCourseResponseDto commitSaveCourseResponseDto = CommitSaveCourseResponseDto.of(123L);

        given(courseSaveFacade.commitSaveCourse(TEST_UUID, commitSaveCourseRequestDto)).willReturn(
                commitSaveCourseResponseDto);

        // when & then
        mockMvc.perform(post("/course/{courseUuid}/commit", TEST_UUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commitSaveCourseRequestDto))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseId").value(123L));

    }

}