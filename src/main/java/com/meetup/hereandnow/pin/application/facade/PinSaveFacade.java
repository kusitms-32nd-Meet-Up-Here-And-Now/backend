package com.meetup.hereandnow.pin.application.facade;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.pin.application.service.save.CouplePinImageSaveService;
import com.meetup.hereandnow.pin.application.service.save.PinImageSaveService;
import com.meetup.hereandnow.pin.application.service.save.PinSaveService;
import com.meetup.hereandnow.pin.application.service.save.PinTagSaveService;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.domain.Place;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinSaveFacade {

    private final PinSaveService pinSaveService;
    private final PinTagSaveService pinTagSaveService;
    private final PinImageSaveService pinImageSaveService;
    private final CouplePinImageSaveService couplePinImageSaveService;

    public void savePinEntityToTable(
            List<PinSaveDto> pinSaveDtos,
            Course course,
            Map<String, Place> placeMap,
            CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        List<Pin> savedPins = pinSaveService.savePins(pinSaveDtos, course, placeMap);

        pinTagSaveService.savePinTags(savedPins, pinSaveDtos);

        pinImageSaveService.savePinImages(savedPins, commitSaveCourseRequestDto.pinImageObjectKeyList());


    }
}
