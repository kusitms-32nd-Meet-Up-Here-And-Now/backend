package com.meetup.hereandnow.tag.domain.entity;

import com.meetup.hereandnow.tag.domain.value.TagGroup;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_group_id", nullable = false)
    private PlaceGroup placeGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_value_id", nullable = false)
    private TagValue tagValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_group", nullable = false, length = 20)
    private TagGroup tagGroup;
}
