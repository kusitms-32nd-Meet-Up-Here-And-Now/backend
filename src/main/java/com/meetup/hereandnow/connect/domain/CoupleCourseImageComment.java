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
@DiscriminatorValue("IMAGE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "couple_course_image_comment")
public class CoupleCourseImageComment extends CoupleCourseComment {

    @Column(nullable = false)
    private String imageUrl; // Object Storage 경로
}