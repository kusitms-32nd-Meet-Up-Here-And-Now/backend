package com.meetup.hereandnow.tag.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "place_group")
public class PlaceGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code; // CT1, P03 등

    @Column(nullable = false, length = 50)
    private String name; // 문화시설, 공공기관 등
}
