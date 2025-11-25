package com.meetup.hereandnow.course.application.service.delete;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

class CourseDeleteServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CourseRepository courseRepository;

    private MockedStatic<SecurityUtils> mockedSecurity;

    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberEntityFixture.getMember();

        mockedSecurity = mockStatic(SecurityUtils.class);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
    }
}