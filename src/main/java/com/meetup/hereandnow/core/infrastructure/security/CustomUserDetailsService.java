package com.meetup.hereandnow.core.infrastructure.security;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(username))
                .orElseThrow(MemberErrorCode.MEMBER_NOT_FOUND::toException);
        return new CustomUserDetails(member, Collections.emptyMap());
    }
}
