package com.meetup.hereandnow.connect.infrastructure.repository;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.value.CoupleStatus;
import com.meetup.hereandnow.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    @Query("""
                    SELECT c
                    FROM Couple c
                    WHERE c.member1 = :member OR c.member2 = :member
            """)
    Optional<Couple> findByMember(@Param("member") Member member);

    @Query("""
            select c
            from Couple c
            where (c.member1 = :member or c.member2 = :member) and c.coupleStatus = :coupleStatus
            """)

    Optional<Couple> findByMemberAndStatus(@Param("member") Member member, @Param("coupleStatus") CoupleStatus coupleStatus);

    @Query("""
            select case when count(c) > 0 then true else false end
            from Couple c
            where c.member1 = :member
               or c.member2 = :member
            """)
    Boolean existsByMember(Member member);
}
