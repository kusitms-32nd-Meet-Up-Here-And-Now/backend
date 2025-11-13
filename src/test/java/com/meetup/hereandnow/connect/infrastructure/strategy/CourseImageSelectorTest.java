package com.meetup.hereandnow.connect.infrastructure.strategy;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseImageSelectorTest {

    private final CourseImageSelector courseImageSelector = new CourseImageSelector();

    @Test
    @DisplayName("최대 3개의 랜덤 이미지를 선택한다")
    void success_max_3_image_in_list() {
        // given
        PinImage image1 = createPinImage("image1.jpg");
        PinImage image2 = createPinImage("image2.jpg");
        PinImage image3 = createPinImage("image3.jpg");
        PinImage image4 = createPinImage("image4.jpg");
        PinImage image5 = createPinImage("image5.jpg");

        Pin pin = createPin(List.of(image1, image2, image3, image4, image5));
        Course course = createCourse(List.of(pin));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(url -> url.endsWith(".jpg"));
    }

    @Test
    @DisplayName("이미지가 3개 미만인 경우 모두 선택한다")
    void success_if_image_count_less_than_3_all_choice() {
        // given
        PinImage image1 = createPinImage("image1.jpg");
        PinImage image2 = createPinImage("image2.jpg");

        Pin pin = createPin(List.of(image1, image2));
        Course course = createCourse(List.of(pin));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder("image1.jpg", "image2.jpg");
    }

    @Test
    @DisplayName("여러 핀의 이미지를 합쳐서 이미지를 선택한다.")
    void success_merge_pin_to_choice_image() {
        // given
        PinImage image1 = createPinImage("image1.jpg");
        PinImage image2 = createPinImage("image2.jpg");

        Pin pin1 = createPin(List.of(image1));
        Pin pin2 = createPin(List.of(image2));

        Course course = createCourse(List.of(pin1, pin2));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("이미지가 없는 경우 빈 리스트를 반환한다.")
    void success_no_image_return_empty_list() {
        // given
        Pin pin = createPin(Collections.emptyList());
        Course course = createCourse(List.of(pin));

        // when
        List<String> result = courseImageSelector.selectRandomImages(course);

        // then
        assertThat(result).isEmpty();
    }

    private PinImage createPinImage(String url) {
        PinImage image = mock(PinImage.class);
        when(image.getImageUrl()).thenReturn(url);
        return image;
    }

    private Pin createPin(List<PinImage> images) {
        Pin pin = mock(Pin.class);
        when(pin.getPinImages()).thenReturn(images);
        return pin;
    }

    private Course createCourse(List<Pin> pins) {
        Course course = mock(Course.class);
        when(course.getPinList()).thenReturn(pins);
        return course;
    }
}

