package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.config.TestSecurityConfiguration;
import com.meetup.hereandnow.course.application.service.delete.CourseDeleteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CourseDeleteController.class)
@Import(TestSecurityConfiguration.class)
class CourseDeleteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseDeleteService courseDeleteService;

    @Test
    void deleteCourse_returnsNoContent_andInvokesService() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(delete("/course/{courseId}", courseId))
                .andExpect(status().isNoContent());

        verify(courseDeleteService, times(1)).courseDeleteById(courseId);
    }
}