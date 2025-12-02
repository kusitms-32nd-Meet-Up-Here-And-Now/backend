package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseCardWithCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentResponseDto;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseCardDtoConverter {

    private final ObjectStorageService objectStorageService;
    private final CourseCommentRepository commentRepository;
    private final CourseScrapRepository courseScrapRepository;

    public List<CourseCardResponseDto> convert(List<Course> courses) {
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        return courses.stream()
                .map(course -> CourseCardResponseDto.from(course, getCourseImages(course)))
                .toList();
    }

    private List<String> getCourseImages(Course course) {
        return course.getPinList().stream()
                .map(pin -> pin.getPinImages().stream().findFirst())
                .flatMap(Optional::stream)
                .map(PinImage::getImageUrl)
                .map(objectStorageService::buildImageUrl)
                .toList();
    }

    public List<CourseCardWithCommentDto> convertWithComment(Member member, List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> courseIds = courses.stream().map(Course::getId).toList();

        // 댓글 맵
        List<CourseComment> allComments = commentRepository.findByCourseIdsWithMember(courseIds);
        Map<Long, List<CourseComment>> commentsMap = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getCourse().getId()));

        // 불러와진 전체 코스 id 중 스크랩한 id들만
        Set<Long> scrappedCourseIds = courseScrapRepository.findScrappedCourseIdsByMemberAndCourseIds(
                member,
                courseIds
        );

        return courses.stream()
                .map(course -> {
                    // 코스 카드
                    CourseCardResponseDto courseCard = CourseCardResponseDto.from(course, getCourseImages(course));

                    List<CourseComment> comments =
                            commentsMap.getOrDefault(course.getId(), Collections.emptyList());
                    // 댓글 카드
                    CourseCommentResponseDto commentResponse = new CourseCommentResponseDto(
                            comments.size(),
                            comments.stream().map(CourseCommentDto::from).toList()
                    );

                    // 스크랩한 코스인지
                    boolean scrapped = scrappedCourseIds.contains(course.getId());
                    return new CourseCardWithCommentDto(courseCard, commentResponse, scrapped);
                })
                .toList();
    }
}
