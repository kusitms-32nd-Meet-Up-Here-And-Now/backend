package com.meetup.hereandnow.connect.infrastructure.repository;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.value.CoupleStatus;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.support.RepositoryTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class CoupleRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        List<Member> memberList = MemberEntityFixture.getMembers(2);

        member1 = memberList.getFirst();
        member2 = memberList.getLast();

        memberRepository.saveAll(memberList);
    }


    @Test
    @DisplayName("특정 유저가 커플인 경우 커플 엔티티를 반환한다.")
    void success_find_by_member() {
        // given
        Couple couple = Couple.builder()
                .member1(member1)
                .member2(member2)
                .coupleStatus(CoupleStatus.ACCEPTED)
                .coupleStartDate(LocalDate.now())
                .coupleBannerImageUrl("image-url")
                .build();

        coupleRepository.save(couple);

        // when
        Optional<Couple> coupleByMember1 = coupleRepository.findByMember(member1);
        Optional<Couple> coupleByMember2 = coupleRepository.findByMember(member2);

        // then
        // 어떤 멤버로 찾아도 커플 정보를 받아온다
        assertThat(coupleByMember1).isPresent();
        assertThat(coupleByMember2).isPresent();

        // 둘의 커플 정보는 같다.
        assertThat(coupleByMember1.get().getId()).isEqualTo(coupleByMember2.get().getId());
    }

    @Test
    @DisplayName("커플 연결 상태와 특정 멤버가 일치하면 커플 엔티티를 반환한다.")
    void success_find_by_member_and_couple_status() {
        // given
        Couple couple = Couple.builder()
                .member1(member1)
                .member2(member2)
                .coupleStatus(CoupleStatus.WAITING)
                .coupleStartDate(LocalDate.now())
                .coupleBannerImageUrl("image-url")
                .build();

        coupleRepository.save(couple);

        // when
        Optional<Couple> coupleByMemberAndStatus = coupleRepository.findByMemberAndStatus(member1, CoupleStatus.WAITING);

        // then
        assertThat(coupleByMemberAndStatus).isPresent();
    }

    @Test
    @DisplayName("특정 멤버가 커플 상태인지 조회한다.")
    void success_is_exists_by_member() {
        // given
        Couple couple = Couple.builder()
                .member1(member1)
                .member2(member2)
                .coupleStatus(CoupleStatus.ACCEPTED)
                .coupleStartDate(LocalDate.now())
                .coupleBannerImageUrl("image-url")
                .build();

        coupleRepository.save(couple);

        // when & then
        assertThat(coupleRepository.existsByMember(member1)).isTrue();
        assertThat(coupleRepository.existsByMember(member2)).isTrue();
    }
}