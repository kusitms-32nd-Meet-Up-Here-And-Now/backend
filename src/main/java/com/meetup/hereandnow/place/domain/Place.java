package com.meetup.hereandnow.place.domain;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.place.infrastructure.converter.PlaceTagListConverter;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "place_street_name_address", nullable = false)
    private String placeStreetNameAddress;

    @Column(name = "place_number_address")
    private String placeNumberAddress;

    @Column(name = "location", columnDefinition = "geography(Point, 4326)")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_group_id", nullable = false)
    private PlaceGroup placeGroup;

    @Column(name = "place_category")
    private String placeCategory;

    @Column(name = "place_rating", precision = 3, scale = 1)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    @Builder.Default
    private BigDecimal placeRating = BigDecimal.valueOf(0.0);

    @Column(name = "place_tags")
    @Convert(converter = PlaceTagListConverter.class)
    @Builder.Default
    private List<String> placeTags = new ArrayList<>();

    @Column(name = "pin_count")
    @Builder.Default
    private Long pinCount = 0L;

    public void updateRating(BigDecimal placeRating, Long pinCount) {
        this.placeRating = placeRating;
        this.pinCount = pinCount;
    }

    public void updateTags(List<String> topTags) {
        this.placeTags = topTags;
    }
}
