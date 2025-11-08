package com.meetup.hereandnow.connect.presentation.controller.info;

import com.meetup.hereandnow.connect.application.info.CoupleInfoSaveService;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoRequestDto;
import com.meetup.hereandnow.connect.presentation.swagger.info.CoupleInfoSaveSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple/connect")
@RequiredArgsConstructor
public class CoupleInfoSaveController implements CoupleInfoSaveSwagger {

    private final CoupleInfoSaveService coupleInfoSaveService;

    @Override
    @PostMapping
    public ResponseEntity<RestResponse<CoupleInfoRequestDto>> saveCoupleInfo(
            @RequestBody CoupleInfoRequestDto coupleInfoRequestDto
    ) {
        coupleInfoSaveService.updateCoupleInfo(coupleInfoRequestDto);
        return ResponseEntity.noContent().build();
    }
}
