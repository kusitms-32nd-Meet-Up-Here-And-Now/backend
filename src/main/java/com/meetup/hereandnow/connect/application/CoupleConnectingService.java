package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.value.CoupleStatus;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleConnectingService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    /**
     *  나에게 온 커플 요청 확인
     */
    @Transactional
    public Optional<CoupleConnectingResponseDto> getPendingRequest() {
        Member current = getCurrentMember();

        return coupleRepository.findByMemberAndStatus(current, CoupleStatus.WAITING)
                .map(couple -> toCoupleConnectingResponseDto(couple, "커플 요청이 있습니다."));
    }

    /**
     *  커플 요청 보내기
     */
    @Transactional
    public CoupleConnectingResponseDto sendRequest(String opponentUsername) {
        Member current = getCurrentMember();
        Member opponent = getOpponentMember(opponentUsername);

        // 이미 커플이거나 대기 중인 요청 존재 시 예외
        if (isExistsCouple(current, opponent)) {
            throw CoupleErrorCode.IS_COUPLE_NOW.toException();
        }

        Couple couple = Couple.builder()
                .member1(current)
                .member2(opponent)
                .build();

        Couple saved = coupleRepository.save(couple);

        // (선택) 알림 전송
        // notificationService.sendCoupleRequestNotification(opponent, current);

        return toCoupleConnectingResponseDto(saved, "요청을 성공적으로 전송했습니다.");
    }

    /**
     *  커플 요청 수락
     */
    @Transactional
    public CoupleConnectingResponseDto approveRequest(Long coupleId) {
        Member current = getCurrentMember();

        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        // 무조건 상대방이 수락을 누른다 상대방의 수락이 아닌 경우 처리
        if (!couple.getMember2().equals(current)) {
            throw CoupleErrorCode.UNAUTHORIZED_APPROVAL.toException();
        }

        if (couple.getCoupleStatus() != CoupleStatus.WAITING) {
            throw CoupleErrorCode.ALREADY_PROCESSED.toException();
        }

        couple.accept();

        return toCoupleConnectingResponseDto(couple, "커플 요청을 수락했습니다.");
    }

    /**
     *  커플 요청 거절
     */
    @Transactional
    public void rejectRequest(Long coupleId) {
        Member current = getCurrentMember();

        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        if (!couple.getMember2().equals(current)) {
            throw CoupleErrorCode.UNAUTHORIZED_REJECTION.toException();
        }

        if (couple.getCoupleStatus() != CoupleStatus.WAITING) {
            throw CoupleErrorCode.ALREADY_PROCESSED.toException();
        }

        coupleRepository.deleteById(coupleId);
    }

    private boolean isExistsCouple(Member member1, Member member2) {
        return coupleRepository.existsByMember(member1) || coupleRepository.existsByMember(member2);
    }

    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMember();
    }

    private Member getOpponentMember(String opponentUsername) {
        return memberRepository.findByUsername(opponentUsername)
                .orElseThrow(MemberErrorCode.MEMBER_NOT_FOUND::toException);
    }

    private CoupleConnectingResponseDto toCoupleConnectingResponseDto(Couple couple, String message){
        return CoupleConnectingResponseDto.from(
                couple.getId(),
                couple.getMember1().getUsername(),
                couple.getMember2().getUsername(),
                message
        );
    }
}
