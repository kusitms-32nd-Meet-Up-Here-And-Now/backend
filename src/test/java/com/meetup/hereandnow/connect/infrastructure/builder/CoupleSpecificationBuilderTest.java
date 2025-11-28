package com.meetup.hereandnow.connect.infrastructure.builder;

import com.meetup.hereandnow.connect.domain.vo.CourseSearchCriteria;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CoupleSpecificationBuilderTest {

    private final CoupleSpecificationBuilder builder = new CoupleSpecificationBuilder();

    @Test
    @DisplayName("연인 필터에서 기본 Specification 에 성공한다.")
    void success_basic_specification_created() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder().build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("평점 필터를 포함하여 Specification을 생성한다.")
    void success_create_specification_including_rating() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .rating(4)
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("키워드 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_keyword() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .keywords(List.of("카페", "맛집"))
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("날짜 범위 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_date() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("지역 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_region() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .region("서울")
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("장소 코드 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_place_code() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .placeCode(List.of("CAFE", "RESTAURANT"))
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("태그 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_tag() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .tags(List.of("데이트", "힐링"))
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("모든 필터가 포함된 Specification을 생성한다.")
    void success_create_specification_including_all() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .rating(5)
                .keywords(List.of("카페"))
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .region("서울")
                .placeCode(List.of("CAFE"))
                .tags(List.of("데이트"))
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("평점이 0 이하인 경우에는 필터에 포함되지 않는다")
    void success_create_specification_not_including_less_than_rating_0() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .rating(0)
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("빈 키워드 리스트는 필터에 포함되지 않는다.")
    void success_create_specification_not_including_empty_keyword_list() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .keywords(List.of())
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("빈 지역 리스트는 필터에 포함되지 않는다.")
    void success_create_specification_not_including_empty_region_list() {
        // given
        Member member = Member.builder().id(1L).build();
        CourseSearchCriteria criteria = CourseSearchCriteria.builder()
                .region("   ")
                .build();

        // when
        Specification<Course> result = builder.build(member, criteria);

        // then
        assertThat(result).isNotNull();
    }
}

