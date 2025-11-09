package com.meetup.hereandnow.connect.presentation.controller.info;

import com.meetup.hereandnow.connect.application.info.CoupleInfoSearchService;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseBannerResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoResponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.info.CoupleInfoSearchSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple")
@RequiredArgsConstructor
public class CoupleInfoSearchController implements CoupleInfoSearchSwagger {

    private final CoupleInfoSearchService coupleInfoSearchService;

    @GetMapping("/info")
    public ResponseEntity<RestResponse<CoupleInfoResponseDto>> getCoupleInfo() {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleInfoSearchService.getCoupleInfoResponse()
                )
        );
    }

    @GetMapping("/banner")
    public ResponseEntity<RestResponse<Slice<CoupleCourseBannerResponseDto>>> getCOnnectingBanner(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleInfoSearchService.getBannerResponse(page, size)
                )
        );
    }
}
