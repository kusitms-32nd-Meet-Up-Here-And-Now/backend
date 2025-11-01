package com.meetup.hereandnow.course.domain.entity;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.course.application.service.converter.CourseTagListConverter;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(name = "course")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_title", length = 128)
    private String courseTitle;

    @Column(name = "course_thumbnail_image", length = 512)
    private String courseThumbnailImage;

    @Column(name = "course_rating", precision = 3, scale = 1)
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    @Builder.Default
    private BigDecimal courseRating = BigDecimal.valueOf(2.5);

    @Column(name = "course_description", length = 1024)
    private String courseDescription;

    @Column(name = "course_tags")
    @Convert(converter = CourseTagListConverter.class)
    @Builder.Default
    private List<String> courseTags = new ArrayList<>();

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "scrap_count", nullable = false)
    @Builder.Default
    private Integer scrapCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(
            mappedBy = "course", fetch = FetchType.LAZY,
            orphanRemoval = true, cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<Pin> pinList = new ArrayList<>();

    public void addPin(Pin pin) {
        this.pinList.add(pin);
    }

    public void updateTags(List<String> topTags) {
        this.courseTags = topTags;
    }

    public void incrementScrapCount() {
        this.scrapCount++;
    }

    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }
}