package com.meetup.hereandnow.connect.repository;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.CoupleStatus;
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
                SELECT c
                FROM Couple c
                WHERE c.member1 = :member1 OR c.member2 = :member2
            """)
    Optional<Couple> findBymember1OrMember2(@Param("member1") Member member1, @Param("member2") Member member2);

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
