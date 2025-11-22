package com.meetup.hereandnow.core.util;

import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Utility class
     */
    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 현재 로그인된 멤버 불러오는 유틸 함수
     * 무분별한 @AuthenticationPrincipal 사용 방지를 위함
     * @return 현재 로그인된 멤버
     */
    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.toException();
        }
        return customUserDetails.member();
    }
}
