package com.meetup.hereandnow.connect.domain;

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
@Table(name = "couple_course_text_comment")
public class CoupleCourseTextComment extends CoupleCourseComment {

    @Column(nullable = false, length = 1000)
    private String content;
}
