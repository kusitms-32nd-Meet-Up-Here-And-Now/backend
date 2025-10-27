package com.meetup.hereandnow.scrap.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.application.service.CourseScrapService;
import com.meetup.hereandnow.scrap.application.service.PlaceScrapService;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScrapFacade {

    private final PlaceScrapService placeScrapService;
    private final CourseScrapService courseScrapService;

    public ScrapResponseDto toggleScrapCourse(Long courseId) {
        Member member = SecurityUtils.getCurrentMember();
        Optional<CourseScrap> courseScrapOptional = courseScrapService.findOptional(member, courseId);
        if (courseScrapOptional.isPresent()) {
            return courseScrapService.deleteScrap(courseScrapOptional.get());
        } else {
            return courseScrapService.scrap(member, courseId);
        }
    }

    public ScrapResponseDto toggleScrapPlace(Long placeId) {
        Member member = SecurityUtils.getCurrentMember();
        Optional<PlaceScrap> placeScrapOptional = placeScrapService.findOptional(member, placeId);
        if (placeScrapOptional.isPresent()) {
            return placeScrapService.deleteScrap(placeScrapOptional.get());
        } else {
            return placeScrapService.scrap(member, placeId);
        }
    }
}
