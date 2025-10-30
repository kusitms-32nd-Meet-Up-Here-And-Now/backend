package com.meetup.hereandnow.place.domain;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(name = "place")
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "place_address")
    private String placeAddress;

    @Column(name = "location", columnDefinition = "geography(Point, 4326)")
    private Point location;

    @Column(name = "place_rating", precision = 3, scale = 1)
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    @Builder.Default
    private BigDecimal placeRating = BigDecimal.valueOf(0.0);

    @Column(name = "pin_count")
    @Builder.Default
    private Long pinCount = 0L;

    public void updateRating(BigDecimal placeRating, Long pinCount) {
        this.placeRating = placeRating;
        this.pinCount = pinCount;
    }
}
