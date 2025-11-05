package com.meetup.hereandnow.connect.infrastructure.repository;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

    @Query("""
            SELECT c
            FROM Couple c
            WHERE c.boyfriendMember = :member OR c.girlfriendMember = :member
    """)
    Optional<Couple> findByMember(@Param("member") Member member);
}
