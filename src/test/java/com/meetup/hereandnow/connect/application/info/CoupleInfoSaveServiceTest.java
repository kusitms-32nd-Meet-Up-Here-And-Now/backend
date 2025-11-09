package com.meetup.hereandnow.connect.application.info;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.request.CoupleInfoRequestDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CoupleInfoSaveServiceTest {

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private CoupleInfoSaveService coupleInfoSaveService;

    private Member member;
    private Member partner;
    private Couple couple;
    private MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).nickname("테스트 유저 1").build();
        partner = Member.builder().id(2L).nickname("테스트 유저 2").build();
        couple = Couple.builder()
                .id(1L)
                .member1(member)
                .member2(partner)
                .build();

        mockedSecurity = mockStatic(SecurityUtils.class);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
    }

    @AfterEach
    void tearDrop() {
        mockedSecurity.close();
    }

    @Test
    @DisplayName("커플 정보 수정 성공 - 시작 날짜만 수정")
    void success_date_change() {
        // given
        LocalDate startDate = LocalDate.of(2025,11,8);
        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(startDate, null);

        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));

        // when
        coupleInfoSaveService.updateCoupleInfo(requestDto);

        // then
        assertThat(couple.getCoupleStartDate()).isEqualTo(startDate);
        verify(coupleRepository).findByMember(member);
        verify(objectStorageService, never()).exists(any());
    }

    @Test
    @DisplayName("커플 정보 수정 성공 - 이미지만 수정")
    void success_image_change() {
        // given
        String imageObjectKey = "/couple/1/banner/image.png";
        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(null, imageObjectKey);

        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(objectStorageService.exists(imageObjectKey)).thenReturn(true);

        // when
        coupleInfoSaveService.updateCoupleInfo(requestDto);

        // then
        assertThat(couple.getCoupleBannerImageUrl()).isEqualTo(imageObjectKey);
        verify(coupleRepository).findByMember(member);
        verify(objectStorageService).exists(imageObjectKey);
    }

    @Test
    @DisplayName("커플 정보 수정 성공 - 시작 날짜와 이미지 모두 수정")
    void success_date_image_all_change() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 8);
        String imageObjectKey = "/couple/1/banner/image.png";
        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(startDate, imageObjectKey);

        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(objectStorageService.exists(imageObjectKey)).thenReturn(true);

        // when
        coupleInfoSaveService.updateCoupleInfo(requestDto);

        // then
        assertThat(couple.getCoupleStartDate()).isEqualTo(startDate);
        assertThat(couple.getCoupleBannerImageUrl()).isEqualTo(imageObjectKey);
        verify(coupleRepository).findByMember(member);
        verify(objectStorageService).exists(imageObjectKey);
    }

    @Test
    @DisplayName("커플 정보 수정 실패 - 저장되지 않은 이미지")
    void fail_not_saved_image() {
        // given
        String imageObjectKey = "/couple/1/banner/notexist.png";
        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(null, imageObjectKey);

        when(objectStorageService.exists(imageObjectKey)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> coupleInfoSaveService.updateCoupleInfo(requestDto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.IS_NOT_SAVED_IMAGE.getMessage());

        verify(objectStorageService).exists(imageObjectKey);
        verify(coupleRepository, never()).findByMember(any());
    }

    @Test
    @DisplayName("커플 정보 수정 실패 - 커플 정보를 찾을 수 없음")
    void fail_not_found_couple() {
        // given
        LocalDate startDate = LocalDate.of(2025, 11, 8);
        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(startDate, null);

        when(coupleRepository.findByMember(member)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> coupleInfoSaveService.updateCoupleInfo(requestDto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());

        verify(coupleRepository).findByMember(member);
    }

    @Test
    @DisplayName("커플 정보 수정 성공 - null 값은 업데이트하지 않음")
    void success_is_null_not_update() {
        // given
        LocalDate originalStartDate = LocalDate.of(2024, 1, 1);
        String originalImageUrl = "/couple/1/banner/original.png";

        Couple coupleWithData = Couple.builder()
                .id(1L)
                .member1(member)
                .member2(partner)
                .coupleStartDate(originalStartDate)
                .coupleBannerImageUrl(originalImageUrl)
                .build();

        CoupleInfoRequestDto requestDto = new CoupleInfoRequestDto(null, null);

        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(coupleWithData));

        // when
        coupleInfoSaveService.updateCoupleInfo(requestDto);

        // then
        assertThat(coupleWithData.getCoupleStartDate()).isEqualTo(originalStartDate);
        assertThat(coupleWithData.getCoupleBannerImageUrl()).isEqualTo(originalImageUrl);
        verify(coupleRepository).findByMember(member);
        verify(objectStorageService, never()).exists(any());
    }
}