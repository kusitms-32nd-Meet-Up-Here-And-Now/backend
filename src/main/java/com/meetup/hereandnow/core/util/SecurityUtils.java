package com.meetup.hereandnow.core.util;

import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.toException();
        }
        return customUserDetails.member();
    }
}
