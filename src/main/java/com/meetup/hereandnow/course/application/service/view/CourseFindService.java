package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseFindService {

    private final CourseRepository courseRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<Course> getNearbyCourses(int page, int size, SortType sort, double lat, double lon) {

        List<Long> courseIds;
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));

        // 리뷰 순 (코스 댓글 순) 경우만 course_comment 조인한 쿼리 사용
        if (sort.equals(SortType.REVIEWS)) {
            Pageable pageable = PageRequest.of(page, size);
            courseIds = courseRepository.findNearbyCourseIdsSortedByCommentCount(point, pageable).getContent();
        } else {
            Pageable pageable = SortUtils.resolveCourseSortNQ(page, size, sort);
            courseIds = courseRepository.findNearbyCourseIds(point, pageable).getContent();
        }

        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            return sortCoursesByIdOrder(
                    // 유저 닉네임, 프사 조회하기 위해 member join fetch 쿼리 사용
                    courseRepository.findCoursesWithDetailsByIds(courseIds),
                    courseIds
            );
        }
    }

    // 조회된 코스들을 기존에 정렬되어야 할 순서로 정렬되게끔 보장해주는 로직
    private List<Course> sortCoursesByIdOrder(List<Course> courses, List<Long> sortedIds) {
        Map<Long, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
        return sortedIds.stream()
                .map(courseMap::get)
                .toList();
    }
}
