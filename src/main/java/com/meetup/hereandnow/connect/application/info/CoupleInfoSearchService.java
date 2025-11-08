package com.meetup.hereandnow.connect.application.info;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoResponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleInfoSearchService {

    private final CoupleRepository coupleRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public CoupleInfoResponseDto getCoupleInfoResponse() {
        Member member = SecurityUtils.getCurrentMember();

        Couple couple = coupleRepository.findByMember(member)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        List<Course> courseList = courseRepository.findByCourseVisitMemberAndMemberIn(
                "연인", List.of(couple.getMember1(), couple.getMember2())
        );

        int placeWithCount = 0;
        int courseWithCount = courseList.size();

        for (Course c : courseList) {
            List<Pin> pinList = c.getPinList();
            placeWithCount += pinList.size();
        }

        return CoupleInfoResponseDto.from(
                couple.getCoupleStartDate(),
                couple,
                placeWithCount,
                courseWithCount
        );
    }
}
