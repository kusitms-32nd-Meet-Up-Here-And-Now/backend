package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.application.service.view.CourseDetailsViewService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseViewFacade {

    private final CourseDetailsViewService courseDetailsViewService;

    @Transactional
    public CourseDetailsResponseDto getCourseDetails(Long courseId) {
        Member member = SecurityUtils.getCurrentMember();
        Course course = courseDetailsViewService.getCourseById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

        if (course.getIsPublic() == false && !course.getMember().getId().equals(member.getId())) {
            throw CourseErrorCode.COURSE_NOT_PUBLIC.toException();
        }

        course.incrementViewCount();
        return CourseDetailsResponseDto.of(member, course, getPinDtoList(member, course));
    }

    public List<PinDetailsResponseDto> getPinDtoList(Member member, Course course) {
        List<PinDetailsResponseDto> pinDtoList = new ArrayList<>();
        Set<Long> scrappedPlaceIds = courseDetailsViewService.getScrappedPlaceIds(member, course);
        int pinIndex = 1;
        for (Pin pin : course.getPinList()) {
            pinDtoList.add(courseDetailsViewService.toPinDetailsDto(pin, pinIndex, scrappedPlaceIds));
            pinIndex++;
        }
        return pinDtoList;
    }
}
