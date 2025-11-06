package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.entity.TagValue;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PinTagSaveServiceTest {

    @Mock
    private PinTagRepository pinTagRepository;
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PinTagSaveService pinTagSaveService;

    private static final Long TEST_ID = 1L;

    private static final String TEST_PIN_POSITIVE = "핀 좋은 점";
    private static final String TEST_PIN_NEGATIVE = "핀 나쁜 점";
    private static final String TEST_PLACE_CODE = "CT1";
    private static final double TEST_PIN_RATING = 4.5;

    private Pin dummyPin;
    private List<String> pinTagList;

    @BeforeEach
    void setUP() {
        dummyPin = Pin.builder().id(TEST_ID).build();
        pinTagList = List.of("분위기 맛집", "산책하기 좋아요");
    }

    @Test
    @DisplayName("핀 태그가 지정되지 않은 경우 저장 로직이 실행되지 않는다.")
    void success_save_pinTags_empty_tags() {

        // given
        List<Pin> pins = List.of(dummyPin);
        PinSaveDto dto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                null
        );

        // when
        pinTagSaveService.savePinTags(pins, List.of(dto));

        // then
        verify(pinTagRepository, org.mockito.Mockito.never()).saveAll(anyList());
    }

    @Test
    void success_save_pinTags_withTags_savesAllTags() {

        // given
        List<Pin> pins = List.of(dummyPin);
        PinSaveDto dto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                pinTagList,
                null
        );

        List<Tag> mockTags = pinTagList.stream()
                .map(tagName -> {
                    Tag mockTag = mock(Tag.class);
                    PlaceGroup mockPlaceGroup = mock(PlaceGroup.class);
                    TagValue mockTagValue = mock(TagValue.class);
                    when(mockPlaceGroup.getCode()).thenReturn(TEST_PLACE_CODE);
                    when(mockTag.getPlaceGroup()).thenReturn(mockPlaceGroup);
                    when(mockTagValue.getName()).thenReturn(tagName);
                    when(mockTag.getTagValue()).thenReturn(mockTagValue);
                    return mockTag;
                })
                .toList();

        given(tagRepository.findByPlaceGroupCodesAndTagNames(Set.of(TEST_PLACE_CODE), new HashSet<>(pinTagList)))
                .willReturn(mockTags);

        // when
        pinTagSaveService.savePinTags(pins, List.of(dto));

        // then
        verify(pinTagRepository).saveAll(anyList());
    }
}

