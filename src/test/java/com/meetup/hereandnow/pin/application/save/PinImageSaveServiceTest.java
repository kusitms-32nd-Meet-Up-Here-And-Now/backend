package com.meetup.hereandnow.pin.application.save;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.meetup.hereandnow.pin.application.service.save.PinImageSaveService;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PinImageSaveServiceTest {

    @Mock
    private PinImageRepository pinImageRepository;

    @InjectMocks
    private PinImageSaveService pinImageSaveService;

    private static final Long TEST_ID = 1L;
    private static final String TEST_OBJECT_KEY = "/img/1.jpg";
    private static final String TEST_OBJECT_KEY2 = "/img/2.jpg";
    private static final int TEST_PIN_INDEX = 0;

    private Pin dummyPin;

    @BeforeEach
    void setUP() {
        dummyPin = Pin.builder().id(TEST_ID).build();
    }

    @Test
    @DisplayName("핀 이미지 objectkey가 없는 경우 저장 로직이 실행되지 않는다.")
    void success_not_contains_objectkeys() {

        // given
        List<Pin> pins = List.of(dummyPin);
        PinImageObjectKeyDto dto = new PinImageObjectKeyDto(TEST_PIN_INDEX, List.of());

        // when
        pinImageSaveService.savePinImages(pins, List.of(dto));

        // then
        verify(pinImageRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("핀 이미지가 정상적으로 저장된다.")
    void savePinImages_withObjectKeys_savesAllImages() {

        // given
        List<Pin> pins = List.of(dummyPin);
        PinImageObjectKeyDto dto = new PinImageObjectKeyDto(TEST_PIN_INDEX, List.of(TEST_OBJECT_KEY, TEST_OBJECT_KEY2));

        when(pinImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        pinImageSaveService.savePinImages(pins, List.of(dto));

        // then
        verify(pinImageRepository).saveAll(anyList());
    }
}

