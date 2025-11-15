package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.place.infrastructure.specification.PlaceSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public Page<Place> searchPlaces(
            Integer rating, List<String> keywords, LocalDate startDate, LocalDate endDate,
            String with, String region, List<String> placeCode, List<String> tags, Pageable pageable
    ) {
        Specification<Place> spec = Specification.where(null);

        if (rating != null && rating > 0) {
            spec = spec.and(PlaceSpecifications.isRatingInRange(rating));
        }
        if (keywords != null && !keywords.isEmpty()) {
            spec = spec.and(PlaceSpecifications.containsKeywords(keywords));
        }
        if (startDate != null || endDate != null) {
            spec = spec.and(PlaceSpecifications.isPinnedByCourseInDateRange(startDate, endDate));
        }
        if (with != null && !with.isBlank()) {
            spec = spec.and(PlaceSpecifications.isPinnedByCourseWith(with));
        }
        if (region != null && !region.isBlank()) {
            spec = spec.and(PlaceSpecifications.isPinnedByCourseInRegion(region));
        }
        if (placeCode != null && !placeCode.isEmpty()) {
            spec = spec.and(PlaceSpecifications.hasPlaceGroupCodeIn(placeCode));
        }
        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(PlaceSpecifications.hasTagIn(tags));
        }
        return placeRepository.findAll(spec, pageable);
    }
}