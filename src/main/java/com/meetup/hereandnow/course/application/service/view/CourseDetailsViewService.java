package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.response.PlaceDetailsResponseDto;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseDetailsViewService {

    private final CourseRepository courseRepository;
    private final PlaceScrapRepository placeScrapRepository;
    private final ObjectStorageService objectStorageService;

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findCourseDetailsById(courseId);
    }

    // 코스 내 장소 중 저장한 장소들의 id만 리턴
    public Set<Long> getScrappedPlaceIds(Member member, Course course) {
        List<Place> uniquePlacesInCourse = course.getPinList().stream()
                .map(Pin::getPlace)
                .distinct()
                .toList();

        Set<Long> scrappedPlaceIds = Collections.emptySet();
        if (!uniquePlacesInCourse.isEmpty()) {
            scrappedPlaceIds = placeScrapRepository.findScrappedPlaceIdsByMemberAndPlaces(
                    member,
                    uniquePlacesInCourse
            );
        }
        return scrappedPlaceIds;
    }

    public PinDetailsResponseDto toPinDetailsDto(Pin pin, int index, Set<Long> scrappedPlaceIds) {
        PlaceDetailsResponseDto placeDto = toPlaceDetailsDto(pin.getPlace(), scrappedPlaceIds);

        List<String> imageUrls = pin.getPinImages().stream()
                .map(pinImage -> objectStorageService.buildImageUrl(pinImage.getImageUrl()))
                .toList();

        return new PinDetailsResponseDto(
                index,
                placeDto,
                imageUrls,
                pin.getPinPositive(),
                pin.getPinNegative()
        );
    }

    private PlaceDetailsResponseDto toPlaceDetailsDto(Place place, Set<Long> scrappedPlaceIds) {
        Point location = place.getLocation();
        Double latitude = (location != null) ? location.getY() : null;
        Double longitude = (location != null) ? location.getX() : null;

        return new PlaceDetailsResponseDto(
                place.getId(),
                place.getPlaceName(),
                place.getPlaceCategory(),
                place.getPlaceStreetNameAddress(),
                latitude,
                longitude,
                place.getPlaceRating().doubleValue(),
                place.getPinCount(),
                place.getPlaceUrl(),
                scrappedPlaceIds.contains(place.getId())
        );
    }

    public void increaseViewCount(Long courseId) {
        courseRepository.increaseViewCount(courseId);
    }
}
