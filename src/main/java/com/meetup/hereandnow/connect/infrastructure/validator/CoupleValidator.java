package com.meetup.hereandnow.connect.infrastructure.validator;

import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoupleValidator {

    private final CoupleRepository coupleRepository;

    public void validate(Member member) {
        if (!coupleRepository.existsByMember(member)) {
            throw CoupleErrorCode.NOT_FOUND_COUPLE.toException();
        }
    }
}

