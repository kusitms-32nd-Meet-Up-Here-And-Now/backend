package com.meetup.hereandnow.pin.domain.entity;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.place.domain.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(name = "pin")
public class Pin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pin_title", length = 128)
    private String pinTitle;

    @Column(name = "pin_thumbnail_image", length = 1024)
    private String pinThumbnailImage;

    @Column(name = "pin_rating", precision = 3, scale = 1)
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    @Builder.Default
    private BigDecimal pinRating = BigDecimal.valueOf(2.5);

    @Column(name = "pin_description", length = 1024)
    private String pinDescription;

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
    private List<PinImage> pinImages = new ArrayList<>();

    public void addPinImage(PinImage pinImage) {
        this.pinImages.add(pinImage);
    }
}
