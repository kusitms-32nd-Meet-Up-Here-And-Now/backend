package com.meetup.hereandnow.member.repository;

import com.meetup.hereandnow.member.domain.Couple;
import com.meetup.hereandnow.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    @Query("""
            SELECT c
            FROM Couple c
            WHERE c.boyfriendMember = :member OR c.girlfriendMember = :member
    """)
    Optional<Couple> findByMember(@Param("member") Member member);
}
