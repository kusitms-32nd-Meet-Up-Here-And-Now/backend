package com.meetup.hereandnow.course.domain.entity;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.member.domain.Couple;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "couple_course_record")
public class CoupleCourseRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description_by_girlfriend")
    private String descriptionByGirlfriend;

    @Column(name = "description_by_boyfriend")
    private String descriptionByBoyfriend;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "couple_id", nullable = false)
    private Couple couple;

    @OneToMany(
            mappedBy = "coupleCourseRecord", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    @Builder.Default
    private List<CoupleCourseImage> coupleCourseImages = new ArrayList<>();

    public void addCoupleCourseImage(CoupleCourseImage coupleCourseImage) {
        this.coupleCourseImages.add(coupleCourseImage);
    }
}
