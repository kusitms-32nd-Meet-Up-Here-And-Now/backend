package com.meetup.hereandnow.pin.domain.entity;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(
        name = "pin_image",
        indexes = {
                @Index(name = "idx_pinimage_pin_id", columnList = "pin_id")
        }
)
public class PinImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pin_id")
    private Pin pin;

    public static PinImage of(String objectKey, Pin pin) {
        return PinImage.builder()
                .imageUrl(objectKey)
                .pin(pin)
                .build();
    }
}
