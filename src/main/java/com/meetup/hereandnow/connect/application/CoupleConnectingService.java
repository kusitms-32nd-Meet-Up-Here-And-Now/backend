package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.connect.dto.request.CoupleConnectingRequestDto;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import com.meetup.hereandnow.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleConnectingService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CoupleConnectingResponseDto connectCouple(CoupleConnectingRequestDto coupleConnectingRequestDto) {

        Member currentMember = getCurrentMember();
        Member opponentMember = getOpponentMember(coupleConnectingRequestDto.opponentUsername());

        if (isExistsCouple(currentMember, opponentMember)) {
            throw CoupleErrorCode.IS_COUPLE_NOW.toException();
        }

        Couple savedCouple = saveCouple(currentMember, opponentMember);

        return CoupleConnectingResponseDto.from(
                savedCouple.getId(),
                savedCouple.getMember1().getUsername(),
                savedCouple.getMember2().getUsername()
        );
    }

    private boolean isExistsCouple(Member member1, Member member2) {
        return coupleRepository.findBymember1OrMember2(member1, member2).isPresent();
    }

    private Couple saveCouple(Member member1, Member member2) {
        Couple couple = Couple.builder()
                .member1(member1)
                .member2(member2)
                .build();

        return coupleRepository.save(couple);
    }

    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMember();
    }

    private Member getOpponentMember(String opponentUsername) {
        return memberRepository.findByUsername(opponentUsername)
                .orElseThrow(MemberErrorCode.MEMBER_NOT_FOUND::toException);
    }
}
