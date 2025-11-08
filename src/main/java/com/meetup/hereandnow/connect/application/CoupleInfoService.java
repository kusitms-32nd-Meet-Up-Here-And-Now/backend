package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoRequestDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleInfoService {

    private final CoupleRepository coupleRepository;
    private final ObjectStorageService objectStorageService;

    @Transactional
    public void updateCoupleInfo(CoupleInfoRequestDto coupleInfoRequestDto) {
        if (
                coupleInfoRequestDto.imageObjectKey() != null
                        && !objectStorageService.exists(coupleInfoRequestDto.imageObjectKey())
        ) {
            throw CoupleErrorCode.IS_NOT_SAVED_IMAGE.toException();
        }

        Member member = SecurityUtils.getCurrentMember();

        Couple couple = coupleRepository.findByMember(member)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        Optional.ofNullable(coupleInfoRequestDto.coupleStartDate())
                .ifPresent(couple::changeStartDate);

        Optional.ofNullable(coupleInfoRequestDto.imageObjectKey())
                .ifPresent(couple::updateImageUrl);
    }
}
