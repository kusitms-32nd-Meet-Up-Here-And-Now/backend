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
@DiscriminatorValue("IMAGE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CoupleCourseImageComment extends CoupleCourseComment {

    @Column(nullable = false)
    private String imageUrl;

    public static CoupleCourseImageComment of(Course course, Member member, String imageUrl) {
        return CoupleCourseImageComment.builder()
                .course(course)
                .member(member)
                .imageUrl(imageUrl)
                .build();
    }
}