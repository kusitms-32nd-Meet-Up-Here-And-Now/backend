package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleConnectingSearchService {

    private final CourseRepository courseRepository;
    private final CoupleRepository coupleRepository;
    private final CourseCommentRepository courseCommentRepository;
    private final CoupleCourseCommentRepository coupleCourseCommentRepository;

    public CoupleRecentArchiveReseponseDto getRecentArchive() {
        Member member = SecurityUtils.getCurrentMember();
        validateCouple(member);

        return courseRepository.findLatestCourse(member, "연인")
                .map(course -> {
                    List<Pin> savedPinList = course.getPinList();

                    List<PinImage> pinImagesInCourse = savedPinList.stream()
                            .flatMap(pin -> pin.getPinImages().stream())
                            .collect(Collectors.toList());

                    Collections.shuffle(pinImagesInCourse);

                    List<String> courseImages = pinImagesInCourse.stream()
                            .map(PinImage::getImageUrl)
                            .limit(3)
                            .toList();

                    int commentCount = getCommentCount(course);

                    return CoupleRecentArchiveReseponseDto.from(course, courseImages, commentCount);
                })
                .orElse(null);
    }


    private void validateCouple(Member member) {
        if(!coupleRepository.existsByMember(member)) {
            throw CoupleErrorCode.NOT_FOUND_COUPLE.toException();
        }
    }


    private int getCommentCount(Course course) {
        int courseCommentCount = courseCommentRepository.countByCourse(course);
        int coupleCourseCommentCount = coupleCourseCommentRepository.countByCourse(course);

        return courseCommentCount + coupleCourseCommentCount;
    }
}
