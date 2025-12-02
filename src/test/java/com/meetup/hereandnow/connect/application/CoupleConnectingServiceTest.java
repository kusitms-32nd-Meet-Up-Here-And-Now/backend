package com.meetup.hereandnow.connect.application;


import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.value.CoupleStatus;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleConnectingServiceTest {

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CoupleConnectingService coupleConnectingService;

    private Member current;
    private Member opponent;
    private Couple couple;

    private MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    void setup() {
        current = Member.builder().id(1L).username("me").build();
        opponent = Member.builder().id(2L).username("you").build();

        couple = Couple.builder()
                .id(1L)
                .member1(current)
                .member2(opponent)
                .coupleStatus(CoupleStatus.WAITING)
                .build();

        mockedSecurity = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    // ========== ① 나에게 온 커플 요청 확인 ==========
    @Test
    @DisplayName("나에게 온 커플 요청이 존재하면 DTO로 반환한다")
    void getPendingRequest_success() {
        // given
        mockCurrentMember(current);
        given(coupleRepository.findByMemberAndStatus(current, CoupleStatus.WAITING))
                .willReturn(Optional.of(couple));

        // when
        Optional<CoupleConnectingResponseDto> result = coupleConnectingService.getPendingRequest();

        // then
        assertThat(result).isPresent();
        assertThat(result.get().myUsername()).isEqualTo("me");
        assertThat(result.get().opponentUsername()).isEqualTo("you");
    }

    // ========== ② 커플 요청 보내기 ==========
    @Test
    @DisplayName("상대방에게 커플 요청을 보낼 수 있다")
    void sendRequest_success() {
        // given
        mockCurrentMember(current);
        given(memberRepository.findByUsername("you")).willReturn(Optional.of(opponent));
        given(coupleRepository.existsByMember(current)).willReturn(false);
        given(coupleRepository.existsByMember(opponent)).willReturn(false);
        given(coupleRepository.save(any(Couple.class))).willReturn(couple);

        // when
        CoupleConnectingResponseDto result = coupleConnectingService.sendRequest("you");

        // then
        assertThat(result.myUsername()).isEqualTo("me");
        assertThat(result.opponentUsername()).isEqualTo("you");
        verify(coupleRepository).save(any(Couple.class));
    }

    @Test
    @DisplayName("이미 커플이거나 대기중이면 예외 발생")
    void sendRequest_alreadyCouple_throwsException() {
        // given
        mockCurrentMember(current);
        given(memberRepository.findByUsername("you")).willReturn(Optional.of(opponent));
        given(coupleRepository.existsByMember(current)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> coupleConnectingService.sendRequest("you"))
                .isInstanceOf(CoupleErrorCode.IS_COUPLE_NOW.toException().getClass());
    }

    // ========== ③ 커플 요청 수락 ==========
    @Test
    @DisplayName("커플 요청 수락 성공")
    void approveRequest_success() {
        // given
        mockCurrentMember(opponent); // member2가 수락자
        given(coupleRepository.findById(1L)).willReturn(Optional.of(couple));

        // when
        CoupleConnectingResponseDto result = coupleConnectingService.approveRequest(1L);

        // then
        assertThat(result.coupleId()).isEqualTo(1L);
        assertThat(couple.getCoupleStatus()).isEqualTo(CoupleStatus.ACCEPTED);
    }

    @Test
    @DisplayName("수락 권한이 없는 사용자가 요청 시 예외 발생")
    void approveRequest_unauthorized_throwsException() {
        // given
        mockCurrentMember(current); // member1이 수락 시도
        given(coupleRepository.findById(1L)).willReturn(Optional.of(couple));

        // when & then
        assertThatThrownBy(() -> coupleConnectingService.approveRequest(1L))
                .isInstanceOf(CoupleErrorCode.UNAUTHORIZED_APPROVAL.toException().getClass());
    }

    // ========== ④ 커플 요청 거절 ==========
    @Test
    @DisplayName("커플 요청 거절 시 DB에서 삭제된다")
    void rejectRequest_success() {
        // given
        mockCurrentMember(opponent);
        given(coupleRepository.findById(1L)).willReturn(Optional.of(couple));

        // when
        coupleConnectingService.rejectRequest(1L);

        // then
        verify(coupleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("거절 권한이 없는 사용자가 요청 시 예외 발생")
    void rejectRequest_unauthorized_throwsException() {
        // given
        mockCurrentMember(current);
        given(coupleRepository.findById(1L)).willReturn(Optional.of(couple));

        // when & then
        assertThatThrownBy(() -> coupleConnectingService.rejectRequest(1L))
                .isInstanceOf(CoupleErrorCode.UNAUTHORIZED_REJECTION.toException().getClass());
    }

    // ======= 헬퍼 =======
    private void mockCurrentMember(Member member) {
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
    }
}
