package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseScrapService {

    private final CourseScrapRepository courseScrapRepository;
    private final CourseRepository courseRepository;

    /**
     * CourseScrap을 생성 또는 삭제합니다.
     */
    @Transactional
    public ScrapResponseDto toggleScrapCourse(Member member, Long courseId) {

        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(ScrapErrorCode.COURSE_NOT_FOUND::toException);

        Optional<CourseScrap> optionalScrap =
                courseScrapRepository.findByMemberIdAndCourseId(member.getId(), courseId);

        if (optionalScrap.isEmpty()) {
            CourseScrap scrap = CourseScrap.builder()
                    .member(member)
                    .course(course)
                    .build();
            course.incrementScrapCount();
            courseScrapRepository.save(scrap);
            return ScrapResponseDto.from(scrap);

        } else {
            course.decrementScrapCount();
            courseScrapRepository.delete(optionalScrap.get());
            return ScrapResponseDto.from();
        }
    }

    /**
     * member가 저장한 CourseScrap을 페이징해 반환합니다.
     */
    public Page<CourseScrap> getScrapsByMember(Member member, Pageable pageable) {
        return courseScrapRepository.findScrapsByMemberWithSort(member, pageable);
    }

    /**
     * 전달된 정렬 값을 pageable로 알맞게 처리합니다. 기본값은 최신순입니다.
     */
    public Pageable resolveSort(int page, int size, String sort) {
        String sortBy = Optional.ofNullable(sort).orElse("");
        String resolvedSortBy;
        if (sortBy.equalsIgnoreCase("scraps")) {
            resolvedSortBy = "course.scrapCount";
        } else {
            resolvedSortBy = "createdAt";
        }
        // TODO: 추후 댓글 수 (리뷰 많은 순) 역순 추가
        return PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, resolvedSortBy)
        );
    }
}
