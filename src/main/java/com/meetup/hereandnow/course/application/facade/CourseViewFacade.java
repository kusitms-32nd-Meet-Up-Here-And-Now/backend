package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.course.application.service.search.CourseSearchService;
import com.meetup.hereandnow.course.application.service.view.CourseCardDtoConverter;
import com.meetup.hereandnow.course.application.service.view.CourseDetailsViewService;
import com.meetup.hereandnow.course.application.service.view.CourseFindService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.*;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseViewFacade {

    private final CourseDetailsViewService courseDetailsViewService;
    private final CourseFindService courseFindService;
    private final CourseCardDtoConverter courseCardDtoConverter;
    private final CourseSearchService courseSearchService;

    /*
    코스를 상세 조회합니다.
     */
    @Transactional
    public CourseDetailsResponseDto getCourseDetails(Long courseId) {
        Member member = SecurityUtils.getCurrentMember();
        Course course = courseDetailsViewService.getCourseById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

        if (course.getIsPublic() == false && !course.getMember().getId().equals(member.getId())) {
            throw CourseErrorCode.COURSE_NOT_PUBLIC.toException();
        }

        courseDetailsViewService.increaseViewCount(courseId);
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

    /*
    근처 코스를 코스 카드 리스트 형태로 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CourseCardResponseDto> getRecommendedCourses(
            int page, int size, SortType sort, double lat, double lon
    ) {
        List<Course> nearbyCourses = courseFindService.getNearbyCourses(page, size, sort, lat, lon);
        return courseCardDtoConverter.convert(nearbyCourses);
    }

    /*
    최근 등록된 코스를 코스 카드+댓글 리스트 형태로 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CourseCardWithCommentDto> getRecentCourses(int page, int size) {
        Pageable pageable = SortUtils.resolveCourseSort(page, size, SortType.RECENT);
        List<Course> courses = courseFindService.getCourses(pageable);
        return courseCardDtoConverter.convertWithComment(SecurityUtils.getCurrentMember(), courses);
    }

    /*
    코스 검색 결과를 적용된 필터와 함께 코스 카드+댓글 리스트 형태로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CourseSearchWithCommentDto getFilteredCourses(
            int page, int size,
            Integer rating, List<String> keyword, LocalDate startDate, LocalDate endDate,
            String with, String region, List<String> placeCode, List<String> tag
    ) {
        Pageable pageable = SortUtils.resolveCourseSort(page, size, SortType.RECENT);
        Page<Course> coursePage = courseSearchService.searchPublicCourses(
                rating, keyword, startDate, endDate, with, region, placeCode, tag, pageable
        );
        SearchFilterDto searchFilterDto = new SearchFilterDto(
                rating, keyword, startDate, endDate, with, region, placeCode, tag
        );
        List<CourseCardWithCommentDto> filteredCourses = coursePage.hasContent() ?
                courseCardDtoConverter.convertWithComment(SecurityUtils.getCurrentMember(), coursePage.getContent())
                : Collections.emptyList();
        return new CourseSearchWithCommentDto(searchFilterDto, filteredCourses);
    }
}
