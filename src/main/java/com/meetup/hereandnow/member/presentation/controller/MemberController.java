package com.meetup.hereandnow.member.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.member.application.MemberService;
import com.meetup.hereandnow.member.dto.MemberInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    public ResponseEntity<RestResponse<MemberInfoResponseDto>> getInfo() {
        return ResponseEntity.ok(
                new RestResponse<>(
                        memberService.getMemberInfo()
                )
        );
    }
}
