package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.CouplePinRecord;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.CouplePinSaveRequestDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.connect.repository.CouplePinRecordRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouplePinRecordSaveService {

    private final CouplePinRecordRepository couplePinRecordRepository;

    public List<CouplePinRecord> saveCouplePinRecords(
            List<Pin> savedPinList, List<PinSaveDto> pinSaveDtos, Couple couple
    ) {
        List<CouplePinRecord> couplePinRecords = new ArrayList<>();

        IntStream.range(0, savedPinList.size())
                .forEach(i -> {
                    Pin savedPin = savedPinList.get(i);
                    PinSaveDto pinSaveDto = pinSaveDtos.get(i);

                    CouplePinSaveRequestDto dto = pinSaveDto.couplePinSaveRequestDto();

                    if(dto == null)
                        return;

                    CouplePinRecord couplePinRecord = CouplePinRecord.builder()
                            .descriptionByBoyfriend(dto.descriptionByBoyfriend())
                            .descriptionByGirlfriend(dto.descriptionByGirlfriend())
                            .pin(savedPin)
                            .couple(couple)
                            .build();
                    couplePinRecords.add(couplePinRecord);
                });

        return couplePinRecordRepository.saveAll(couplePinRecords);
    }
}
