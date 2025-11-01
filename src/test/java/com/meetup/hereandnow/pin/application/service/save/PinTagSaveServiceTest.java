package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    private static final String TEST_PIN_TITLE = "핀 제목";
    private static final String TEST_PIN_DESC = "핀 설명";
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
                TEST_PIN_TITLE, TEST_PIN_RATING, TEST_PIN_DESC,
                TEST_PLACE_CODE, List.of(), null, null
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
                TEST_PIN_TITLE, TEST_PIN_RATING, TEST_PIN_DESC,
                TEST_PLACE_CODE, pinTagList, null, null
        );

        when(pinTagRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        given(tagRepository.findByPlaceGroupAndTagName(TEST_PLACE_CODE, pinTagList.getFirst()))
                .willReturn(Optional.of(mock(Tag.class)));
        given(tagRepository.findByPlaceGroupAndTagName(TEST_PLACE_CODE, pinTagList.get(1)))
                .willReturn(Optional.of(mock(Tag.class)));

        // when
        pinTagSaveService.savePinTags(pins, List.of(dto));

        // then
        verify(pinTagRepository).saveAll(anyList());
    }
}

