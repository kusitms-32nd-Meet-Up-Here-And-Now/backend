package com.meetup.hereandnow.course.application.service.delete;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseDeleteService {

    private final CourseRepository courseRepository;

    @Transactional
    public void courseDeleteById(Long courseId) {

       Member member = SecurityUtils.getCurrentMember();

       Course course = courseRepository.findById(courseId)
               .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

       if(!course.getMember().getId().equals(member.getId())) {
           throw CourseErrorCode.IS_NOT_YOURS.toException();
       }

       courseRepository.delete(course);
    }
}
