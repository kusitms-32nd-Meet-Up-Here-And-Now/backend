package com.meetup.hereandnow.member.domain;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "couple",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"boyfriend_member_id"}),
                @UniqueConstraint(columnNames = {"girlfriend_member_id"})
        }
)
public class Couple extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boyfriend_member_id", nullable = false)
    private Member boyfriendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "girlfriend_member_id", nullable = false)
    private Member girlfriendMember;
}
