package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCardDtoConverterTest {

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private CourseCardDtoConverter courseCardDtoConverter;

    private MockedStatic<CourseCardResponseDto> mockedDto;

    @BeforeEach
    void setUp() {
        mockedDto = mockStatic(CourseCardResponseDto.class);
    }

    @AfterEach
    void tearDown() {
        mockedDto.close();
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
        mockedDto.verify(() -> CourseCardResponseDto.from(any(), any()), never());
    }

    @Test
    @DisplayName("코스에 핀이 없으면 빈 이미지 리스트로 DTO를 생성한다")
    void convert_with_course_no_pins() {

        // given
        Course mockCourse = mock(Course.class);
        CourseCardResponseDto mockDto = mock(CourseCardResponseDto.class);
        List<String> emptyImageList = Collections.emptyList();

        given(mockCourse.getPinList()).willReturn(Collections.emptyList());
        mockedDto.when(() -> CourseCardResponseDto.from(mockCourse, emptyImageList)).thenReturn(mockDto);

        // when
        List<CourseCardResponseDto> result = courseCardDtoConverter.convert(List.of(mockCourse));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(mockDto);

        verify(mockCourse).getPinList();
        verify(objectStorageService, never()).buildImageUrl(anyString());
        mockedDto.verify(() -> CourseCardResponseDto.from(mockCourse, emptyImageList));
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
        mockedDto.when(() -> CourseCardResponseDto.from(mockCourse, expectedImageList)).thenReturn(mockDto);

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

        mockedDto.verify(() -> CourseCardResponseDto.from(mockCourse, expectedImageList));
    }
}