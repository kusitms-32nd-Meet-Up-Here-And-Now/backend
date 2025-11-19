package com.meetup.hereandnow.connect.domain;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("TEXT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoupleCourseTextComment extends CoupleCourseComment {

    @Column(length = 1000)
    private String content;

    public static CoupleCourseTextComment of(Course course, Member member, String content) {
        return CoupleCourseTextComment.builder()
                .course(course)
                .member(member)
                .content(content)
                .build();
    }
}
