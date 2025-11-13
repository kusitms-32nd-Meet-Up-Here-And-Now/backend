package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.infrastructure.aggregator.CommentCountAggregator;
import com.meetup.hereandnow.connect.infrastructure.builder.CoupleSpecificationBuilder;
import com.meetup.hereandnow.connect.infrastructure.strategy.CourseImageSelector;
import com.meetup.hereandnow.connect.infrastructure.validator.CoupleValidator;
import com.meetup.hereandnow.connect.domain.vo.CourseSearchCriteria;
import com.meetup.hereandnow.connect.domain.vo.CourseVisitType;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseFolderResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseSearchFilterDto;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseSearchResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleConnectingSearchService {

    private final CourseRepository courseRepository;
    private final CoupleValidator coupleValidator;
    private final CourseImageSelector imageSelector;
    private final CommentCountAggregator commentCountAggregator;
    private final CoupleSpecificationBuilder specificationBuilder;

    @Transactional
    public CoupleRecentArchiveReseponseDto getRecentArchive() {
        Member member = SecurityUtils.getCurrentMember();
        coupleValidator.validate(member);

        return courseRepository.findLatestCourse(member, CourseVisitType.COUPLE.getValue())
                .map(course -> {
                    List<String> courseImages = imageSelector.selectRandomImages(course);
                    int commentCount = commentCountAggregator.aggregate(course);
                    return CoupleRecentArchiveReseponseDto.from(course, courseImages, commentCount);
                })
                .orElse(null);
    }

    public CoupleCourseSearchResponseDto getCourseFolder(
            int page, int size,
            Integer rating, List<String> keywords,
            LocalDate startDate, LocalDate endDate, String region,
            List<String> placeCode, List<String> tags
    ) {
        Member member = SecurityUtils.getCurrentMember();

        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .rating(rating)
                .keywords(keywords)
                .startDate(startDate)
                .endDate(endDate)
                .region(region)
                .placeCode(placeCode)
                .tags(tags)
                .build();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Course> coursePage = searchCourses(member, criteria, pageRequest);

        List<CoupleCourseFolderResponseDto> courses = coursePage.getContent()
                .stream()
                .map(course -> CoupleCourseFolderResponseDto.from(course, commentCountAggregator.aggregate(course)))
                .toList();

        CoupleCourseSearchFilterDto filterDto = new CoupleCourseSearchFilterDto(
                rating, keywords, startDate, endDate, region, placeCode, tags
        );

        return new CoupleCourseSearchResponseDto(filterDto, courses);
    }

    private Page<Course> searchCourses(Member member, CourseSearchCriteria criteria, PageRequest pageRequest) {
        return courseRepository.findAll(specificationBuilder.build(member, criteria), pageRequest);
    }
}

