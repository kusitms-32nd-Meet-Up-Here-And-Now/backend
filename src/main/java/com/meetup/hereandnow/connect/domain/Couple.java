package com.meetup.hereandnow.connect.domain;

import com.meetup.hereandnow.core.infrastructure.entity.BaseEntity;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Builder;
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
                @UniqueConstraint(columnNames = {"member_1"}),
                @UniqueConstraint(columnNames = {"member_2"})
        }
)
public class Couple extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_1", nullable = false)
    private Member member1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_2", nullable = false)
    private Member member2;

    @Column(name = "couple_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CoupleStatus coupleStatus = CoupleStatus.WAITING;

    public void accept() {
        this.coupleStatus = CoupleStatus.ACCEPTED;
    }
}
