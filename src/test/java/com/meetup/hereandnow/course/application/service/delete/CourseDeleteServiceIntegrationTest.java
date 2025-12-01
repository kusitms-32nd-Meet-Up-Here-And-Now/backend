package com.meetup.hereandnow.course.application.service.delete;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class CourseDeleteServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CourseDeleteService courseDeleteService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private CourseCommentRepository courseCommentRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberEntityFixture.getMember();
        memberRepository.save(member);

        mockedSecurity = mockStatic(SecurityUtils.class);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Test
    @DisplayName("코스 식별자로 코스 삭제 성공 (IntegrationTest)")
    void success_course_delete_by_course_id() {
        // given
        PlaceGroup placeGroup = placeGroupRepository.findByCode("FD6").orElse(null);
        Place place = PlaceEntityFixture.getPlace(placeGroup);
        placeRepository.save(place);

        Course course = CourseEntityFixture.getCourse(member);
        courseRepository.save(course);

        Pin pin = PinEntityFixture.getPin(course, place);
        pinRepository.save(pin);

        courseCommentRepository.save(CourseCommentFixture.getCourseComment(course, member));

        // when
        courseDeleteService.courseDeleteById(course.getId());

        // then
        assertThat(courseRepository.existsById(course.getId())).isFalse();

        assertThat(pinRepository.existsById(pin.getId())).isFalse();
        assertThat(courseCommentRepository.findByCourse(course)).isEmpty();

        assertThat(memberRepository.existsById(member.getId())).isTrue();
        assertThat(placeRepository.existsById(place.getId())).isTrue();
    }
}
