package com.meetup.hereandnow.pin.domain.entity;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.place.domain.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "pin",
        indexes = {
                @Index(name = "idx_pin_place_id", columnList = "place_id")
        }
)
public class Pin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pin_rating", precision = 3, scale = 1)
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    @Builder.Default
    private BigDecimal pinRating = BigDecimal.valueOf(2.5);

    @Column(name = "pin_positive", nullable = false)
    private String pinPositive;

    @Column(name = "pin_negative", nullable = false)
    private String pinNegative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @OneToMany(
            mappedBy = "pin", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true
    )
    @BatchSize(size = 100)
    @Builder.Default
    private List<PinImage> pinImages = new ArrayList<>();

    @OneToMany(
            mappedBy = "pin", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true
    )
    @BatchSize(size = 100)
    @Builder.Default
    private List<PinTag> pinTags = new ArrayList<>();

    public void addPinTag(PinTag pinTag) {
        this.pinTags.add(pinTag);
    }

    public void addPinImage(PinImage pinImage) {
        this.pinImages.add(pinImage);
    }
}
