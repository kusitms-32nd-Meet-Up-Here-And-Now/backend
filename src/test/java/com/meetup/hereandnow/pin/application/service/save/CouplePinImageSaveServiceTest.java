package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.connect.domain.CouplePinImage;
import com.meetup.hereandnow.connect.domain.CouplePinRecord;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.connect.infrastructure.repository.CouplePinImageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouplePinImageSaveServiceTest {

    @Mock
    private CouplePinImageRepository couplePinImageRepository;

    @InjectMocks
    private CouplePinImageSaveService couplePinImageSaveService;

    private CouplePinRecord couplePinRecord1;
    private CouplePinRecord couplePinRecord2;

    @BeforeEach
    void setUp() {
        couplePinRecord1 = CouplePinRecord.builder().id(1L).build();
        couplePinRecord2 = CouplePinRecord.builder().id(2L).build();
    }

    @Test
    @DisplayName("커플 핀 기록에 대하여 이미지 저장이 순차적으로 이뤄진다.")
    void success_couple_pin_image_saved_by_order() {
        // given
        List<CouplePinRecord> records = List.of(couplePinRecord1, couplePinRecord2);

        List<PinImageObjectKeyDto> dtoList = List.of(
                new PinImageObjectKeyDto(0,
                        List.of("/pins/1/original.jpg"),
                        List.of("/pins/1/couple-1.jpg", "/pins/1/couple-2.jpg")),
                new PinImageObjectKeyDto(1,
                        List.of("/pins/2/original.jpg"),
                        List.of("/pins/2/couple-1.jpg"))
        );

        // when
        couplePinImageSaveService.saveCouplePinImages(records, dtoList);

        // then
        ArgumentCaptor<List<CouplePinImage>> captor = ArgumentCaptor.forClass(List.class);
        verify(couplePinImageRepository).saveAll(captor.capture());

        List<CouplePinImage> savedImages = captor.getValue();
        assertThat(savedImages).hasSize(3);

        assertThat(couplePinRecord1.getCouplePinImages()).hasSize(2);
        assertThat(couplePinRecord2.getCouplePinImages()).hasSize(1);

        assertThat(couplePinRecord1.getCouplePinImages())
                .extracting(CouplePinImage::getCouplePinImageUrl)
                .containsExactly("/pins/1/couple-1.jpg", "/pins/1/couple-2.jpg");
    }

    @Test
    @DisplayName("커플 이미지 키가 null 이면 저장을 건너 뛴다.")
    void success_pass_if_couple_image_key_is_null() {
        // given
        List<CouplePinRecord> records = List.of(couplePinRecord1);

        List<PinImageObjectKeyDto> dtoList = List.of(
                new PinImageObjectKeyDto(0, List.of("/pins/1/original.jpg"), null)
        );

        // when
        couplePinImageSaveService.saveCouplePinImages(records, dtoList);

        // then
        verify(couplePinImageRepository, never()).saveAll(any());
        assertThat(couplePinRecord1.getCouplePinImages()).isEmpty();
    }

    @Test
    void shouldHandleIndexMismatchSafely() {
        // given
        List<CouplePinRecord> records = List.of(couplePinRecord1, couplePinRecord2);

        List<PinImageObjectKeyDto> dtoList = List.of(
                new PinImageObjectKeyDto(0, List.of(), null),
                new PinImageObjectKeyDto(1, List.of(), List.of("/pins/1/couple-1.jpg")),
                new PinImageObjectKeyDto(2, List.of(), null),
                new PinImageObjectKeyDto(3, List.of(), List.of("/pins/3/couple-3.jpg"))
        );

        // when
        assertDoesNotThrow(() -> couplePinImageSaveService.saveCouplePinImages(records, dtoList));
    }
}