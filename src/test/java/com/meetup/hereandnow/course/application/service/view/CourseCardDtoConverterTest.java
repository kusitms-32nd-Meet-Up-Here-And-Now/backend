package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseCardWithCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentDto;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCardDtoConverterTest {

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private CourseCommentRepository commentRepository;

    @Mock
    private CourseScrapRepository courseScrapRepository;

    @InjectMocks
    private CourseCardDtoConverter courseCardDtoConverter;

    private MockedStatic<CourseCardResponseDto> courseCardDto;
    private MockedStatic<CourseCommentDto> courseCommentDto;

    @BeforeEach
    void setUp() {
        courseCardDto = mockStatic(CourseCardResponseDto.class);
        courseCommentDto = mockStatic(CourseCommentDto.class);
    }

    @AfterEach
    void tearDown() {
        courseCardDto.close();
        courseCommentDto.close();
    }

    @Test
    @DisplayName("빈 코스 리스트가 주어지면 빈 DTO 리스트를 반환한다")
    void convert_with_empty_list_returns_empty_list() {

        // given
        List<Course> emptyList = Collections.emptyList();

        // when
        List<CourseCardResponseDto> result = courseCardDtoConverter.convert(emptyList);

        // then
        assertThat(result).isEmpty();

        verify(objectStorageService, never()).buildImageUrl(anyString());
        courseCardDto.verify(() -> CourseCardResponseDto.from(any(), any()), never());
    }

    @Test
    @DisplayName("코스에 핀이 없으면 빈 이미지 리스트로 DTO를 생성한다")
    void convert_with_course_no_pins() {

        // given
        Course mockCourse = mock(Course.class);
        CourseCardResponseDto mockDto = mock(CourseCardResponseDto.class);
        List<String> emptyImageList = Collections.emptyList();

        given(mockCourse.getPinList()).willReturn(Collections.emptyList());
        courseCardDto.when(() -> CourseCardResponseDto.from(mockCourse, emptyImageList)).thenReturn(mockDto);

        // when
        List<CourseCardResponseDto> result = courseCardDtoConverter.convert(List.of(mockCourse));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(mockDto);

        verify(mockCourse).getPinList();
        verify(objectStorageService, never()).buildImageUrl(anyString());
        courseCardDto.verify(() -> CourseCardResponseDto.from(mockCourse, emptyImageList));
    }

    @Test
    @DisplayName("각 핀의 첫 번째 이미지만을 필터링하여 DTO를 생성한다")
    void convert_with_courses_filters_and_maps_first_image_of_each_pin() {

        // given
        Course mockCourse = mock(Course.class);

        Pin pin1 = mock(Pin.class); // 2개의 이미지 (img1a, img1b)
        Pin pin2 = mock(Pin.class); // 이미지가 없음
        Pin pin3 = mock(Pin.class); // 1개의 이미지 (img3a)

        PinImage img1a = mock(PinImage.class);
        PinImage img1b = mock(PinImage.class); // 최종 반환 시 무시되는 이미지
        PinImage img3a = mock(PinImage.class);

        CourseCardResponseDto mockDto = mock(CourseCardResponseDto.class);

        given(mockCourse.getPinList()).willReturn(List.of(pin1, pin2, pin3));

        given(pin1.getPinImages()).willReturn(List.of(img1a, img1b));
        given(pin2.getPinImages()).willReturn(Collections.emptyList());
        given(pin3.getPinImages()).willReturn(List.of(img3a));

        given(img1a.getImageUrl()).willReturn("img1a.jpg");
        given(img3a.getImageUrl()).willReturn("img3a.jpg");

        given(objectStorageService.buildImageUrl("img1a.jpg")).willReturn("http://img1a.jpg");
        given(objectStorageService.buildImageUrl("img3a.jpg")).willReturn("http://img3a.jpg");

        List<String> expectedImageList = List.of("http://img1a.jpg", "http://img3a.jpg");
        courseCardDto.when(() -> CourseCardResponseDto.from(mockCourse, expectedImageList)).thenReturn(mockDto);

        // when
        List<CourseCardResponseDto> result = courseCardDtoConverter.convert(List.of(mockCourse));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(mockDto);

        verify(mockCourse).getPinList();
        verify(pin1).getPinImages();
        verify(pin2).getPinImages();
        verify(pin3).getPinImages();

        verify(img1a).getImageUrl();
        verify(img3a).getImageUrl();
        verify(img1b, never()).getImageUrl();

        verify(objectStorageService).buildImageUrl("img1a.jpg");
        verify(objectStorageService).buildImageUrl("img3a.jpg");

        courseCardDto.verify(() -> CourseCardResponseDto.from(mockCourse, expectedImageList));
    }

    @Test
    @DisplayName("convertWithComment: null 리스트가 주어지면 빈 리스트를 반환한다")
    void convert_with_comment_returns_empty_list_for_null() {

        // given
        List<Course> nullCourses = null;

        // when
        Member mockMember = mock(Member.class);
        List<CourseCardWithCommentDto> result = courseCardDtoConverter.convertWithComment(mockMember, nullCourses);

        // then
        assertThat(result).isEmpty();

        verifyNoInteractions(commentRepository, courseScrapRepository, objectStorageService);
        courseCardDto.verifyNoInteractions();
        courseCommentDto.verifyNoInteractions();
    }

    @Test
    @DisplayName("convertWithComment: 빈 리스트가 주어지면 빈 리스트를 반환한다")
    void convert_with_comment_returns_empty_list_for_empty() {

        // given
        Member mockMember = mock(Member.class);
        List<Course> emptyCourses = Collections.emptyList();

        // when
        List<CourseCardWithCommentDto> result = courseCardDtoConverter.convertWithComment(mockMember, emptyCourses);

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(commentRepository, courseScrapRepository, objectStorageService);
    }

    @Test
    @DisplayName("convertWithComment: 코스를 DTO 리스트로 올바르게 변환한다")
    void convert_with_comment() {

        // given
        Course mockCourse1 = mock(Course.class);
        Course mockCourse2 = mock(Course.class);
        given(mockCourse1.getId()).willReturn(1L);
        given(mockCourse2.getId()).willReturn(2L);
        List<Course> courses = List.of(mockCourse1, mockCourse2);
        List<Long> courseIds = List.of(1L, 2L);

        CourseComment comment1 = mock(CourseComment.class);
        CourseComment comment2 = mock(CourseComment.class);

        Course courseFromComment = mock(Course.class);
        given(courseFromComment.getId()).willReturn(1L);
        given(comment1.getCourse()).willReturn(courseFromComment);
        given(comment2.getCourse()).willReturn(courseFromComment);

        given(commentRepository.findByCourseIdsWithMember(courseIds)).willReturn(List.of(comment1, comment2));

        Member mockMember = mock(Member.class);
        Set<Long> scrappedIds = Set.of(1L);
        given(courseScrapRepository.findScrappedCourseIdsByMemberAndCourseIds(mockMember, courseIds)).willReturn(scrappedIds);

        Pin pin1 = mock(Pin.class);
        PinImage img1 = mock(PinImage.class);
        given(mockCourse1.getPinList()).willReturn(List.of(pin1)); // Course1 핀 1개
        given(pin1.getPinImages()).willReturn(List.of(img1));  // Pin1 이미지 1개
        given(img1.getImageUrl()).willReturn("img1.jpg");
        given(objectStorageService.buildImageUrl("img1.jpg")).willReturn("http://img1.jpg");
        List<String> images1 = List.of("http://img1.jpg");

        given(mockCourse2.getPinList()).willReturn(Collections.emptyList()); // Course2 핀 없음
        List<String> images2 = Collections.emptyList();

        CourseCardResponseDto mockCard1 = mock(CourseCardResponseDto.class);
        CourseCardResponseDto mockCard2 = mock(CourseCardResponseDto.class);
        courseCardDto.when(() -> CourseCardResponseDto.from(mockCourse1, images1)).thenReturn(mockCard1);
        courseCardDto.when(() -> CourseCardResponseDto.from(mockCourse2, images2)).thenReturn(mockCard2);

        CourseCommentDto mockCommentDto1 = mock(CourseCommentDto.class);
        CourseCommentDto mockCommentDto2 = mock(CourseCommentDto.class);
        courseCommentDto.when(() -> CourseCommentDto.from(comment1)).thenReturn(mockCommentDto1);
        courseCommentDto.when(() -> CourseCommentDto.from(comment2)).thenReturn(mockCommentDto2);

        // when
        List<CourseCardWithCommentDto> result = courseCardDtoConverter.convertWithComment(mockMember, courses);

        // then
        assertThat(result).hasSize(2);

        CourseCardWithCommentDto dto1 = result.getFirst();
        assertThat(dto1.courseCard()).isSameAs(mockCard1);
        assertThat(dto1.scrapped()).isTrue();
        assertThat(dto1.comment().count()).isEqualTo(2);
        assertThat(dto1.comment().comments()).containsExactly(mockCommentDto1, mockCommentDto2);

        CourseCardWithCommentDto dto2 = result.get(1);
        assertThat(dto2.courseCard()).isSameAs(mockCard2);
        assertThat(dto2.scrapped()).isFalse();
        assertThat(dto2.comment().count()).isEqualTo(0);
        assertThat(dto2.comment().comments()).isEmpty();

        verify(commentRepository).findByCourseIdsWithMember(courseIds);
        verify(courseScrapRepository).findScrappedCourseIdsByMemberAndCourseIds(mockMember, courseIds);
        verify(objectStorageService).buildImageUrl("img1.jpg");

        courseCardDto.verify(() -> CourseCardResponseDto.from(mockCourse1, images1));
        courseCardDto.verify(() -> CourseCardResponseDto.from(mockCourse2, images2));
        courseCommentDto.verify(() -> CourseCommentDto.from(comment1));
        courseCommentDto.verify(() -> CourseCommentDto.from(comment2));
    }
}