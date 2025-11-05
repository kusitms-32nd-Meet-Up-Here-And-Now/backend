package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.CouplePinRecord;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.CouplePinSaveRequestDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.connect.repository.CouplePinRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CouplePinRecordSaveServiceTest {

    @Mock
    private CouplePinRecordRepository couplePinRecordRepository;

    @InjectMocks
    private CouplePinRecordSaveService couplePinRecordSaveService;

    private Couple couple;
    private Pin pin1;
    private Pin pin2;

    private static final String TEST_PIN_POSITIVE = "핀 좋은 점";
    private static final String TEST_PIN_NEGATIVE = "핀 나쁜 점";
    private static final String TEST_PLACE_CODE = "CT1";
    private static final double TEST_PIN_RATING = 4.5;

    @BeforeEach
    void setUp() {
        couple = Couple.builder().id(1L).build();

        pin1 = Pin.builder().id(1L).build();
        pin2 = Pin.builder().id(2L).build();
    }

    @Test
    @DisplayName("커플 기록 정보가 없는 경우에는 저장이 이뤄지지 않는다")
    void success_no_couple_records() {
        // given
        List<Pin> savedPin = List.of(pin1, pin2);

        PinSaveDto dto1 = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                null,
                null
        );

        CouplePinSaveRequestDto coupleDto = new CouplePinSaveRequestDto("여친메모", "남친메모");

        PinSaveDto dto2 = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                coupleDto,
                null
        );

        List<PinSaveDto> pinSaveDtos = List.of(dto1, dto2);

        given(couplePinRecordRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        List<CouplePinRecord> result = couplePinRecordSaveService.saveCouplePinRecords(savedPin, pinSaveDtos, couple);

        // then
        assertThat(result).hasSize(1);
        CouplePinRecord record = result.getFirst();
        assertThat(record.getDescriptionByBoyfriend()).isEqualTo("남친메모");
        assertThat(record.getDescriptionByGirlfriend()).isEqualTo("여친메모");
        assertThat(record.getPin()).isEqualTo(pin2);
        assertThat(record.getCouple()).isEqualTo(couple);

        verify(couplePinRecordRepository, times(1)).saveAll(anyList());
    }
}