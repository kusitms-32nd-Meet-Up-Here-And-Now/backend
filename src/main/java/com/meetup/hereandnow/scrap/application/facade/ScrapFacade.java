package com.meetup.hereandnow.scrap.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.application.service.PlaceCardDtoConverter;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceCardResponseDto;
import com.meetup.hereandnow.scrap.application.service.CourseScrapService;
import com.meetup.hereandnow.scrap.application.service.PlaceScrapService;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScrapFacade {

    private final PlaceScrapService placeScrapService;
    private final CourseScrapService courseScrapService;
    private final PlaceCardDtoConverter placeCardDtoConverter;

    @Transactional
    public ScrapResponseDto toggleScrapCourse(Long courseId) {
        Member member = SecurityUtils.getCurrentMember();
        Optional<CourseScrap> courseScrapOptional = courseScrapService.findOptional(member, courseId);
        if (courseScrapOptional.isPresent()) {
            return courseScrapService.deleteScrap(courseScrapOptional.get());
        } else {
            return courseScrapService.scrap(member, courseId);
        }
    }

    @Transactional
    public ScrapResponseDto toggleScrapPlace(Long placeId) {
        Member member = SecurityUtils.getCurrentMember();
        return placeScrapService.toggleScrapPlace(member, placeId);
    }

    @Transactional(readOnly = true)
    public List<PlaceCardResponseDto> getScrappedPlaces(int page, int size, String sort) {
        Member member = SecurityUtils.getCurrentMember();
        Pageable resolvedPageable = placeScrapService.resolveSort(page, size, sort);
        Page<PlaceScrap> scrapPage = placeScrapService.getScrapsByMember(member, resolvedPageable);
        List<Place> places = scrapPage.getContent().stream().map(PlaceScrap::getPlace).toList();
        return placeCardDtoConverter.convert(places);
    }
}
