package com.meetup.hereandnow.member.application;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.dto.MemberInfoResponseDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final CoupleRepository coupleRepository;

    public MemberInfoResponseDto getMemberInfo() {
        Member member = SecurityUtils.getCurrentMember();

        Optional<Couple> couple = coupleRepository.findByMember(member);

        return MemberInfoResponseDto.from(member, couple.orElse(null));
    }
}
