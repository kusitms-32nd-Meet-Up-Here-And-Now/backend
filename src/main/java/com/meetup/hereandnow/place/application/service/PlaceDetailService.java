package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.PlaceInfoResponseDto;
import com.meetup.hereandnow.place.exception.PlaceErrorCode;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceDetailService {

    private final PlaceRepository placeRepository;
    private final PinRepository pinRepository;

    @Transactional(readOnly = true)
    public PlaceInfoResponseDto getPlaceDetail(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(PlaceErrorCode.NOT_FOUND_PLACE::toException);

        List<Pin> pinList = pinRepository.findAllByPlace(place);

        List<String> placeTagList = place.getPlaceTags();
        List<String> bannerImageList = extractBannerImages(pinList);
        List<String> placeInfoImageList = extractPlaceInfoImages(pinList);
        List<String> placePositiveList = extractPositiveReviews(pinList);
        List<String> placeNegativeList = extractNegativeReviews(pinList);
        List<CourseCardResponseDto> courseList = buildRelatedCourses(pinList);

        PlaceCardResponseDto placeCardResponseDto = buildPlaceCard(place, bannerImageList);

        return new PlaceInfoResponseDto(
                placeCardResponseDto,
                placeTagList,
                bannerImageList,
                placeInfoImageList,
                placePositiveList,
                placeNegativeList,
                courseList
        );
    }

    /**
     * 배너 이미지 추출 (상위 5개)
     */
    private List<String> extractBannerImages(List<Pin> pinList) {
        return pinList.stream()
                .flatMap(pin -> pin.getPinImages().stream())
                .map(PinImage::getImageUrl)
                .limit(5)
                .toList();
    }

    /**
     * 지도 하단 이미지 추출 (상위 10개)
     */
    private List<String> extractPlaceInfoImages(List<Pin> pinList) {
        return pinList.stream()
                .flatMap(pin -> pin.getPinImages().stream())
                .map(PinImage::getImageUrl)
                .limit(10)
                .toList();
    }

    /**
     * 장소 좋았던 점 리스트 추출
     */
    private List<String> extractPositiveReviews(List<Pin> pinList) {
        return pinList.stream()
                .map(Pin::getPinPositive)
                .filter(positive -> positive != null && !positive.isBlank())
                .distinct()
                .toList();
    }

    /**
     * 장소 아쉬운 점 리스트 추출
     */
    private List<String> extractNegativeReviews(List<Pin> pinList) {
        return pinList.stream()
                .map(Pin::getPinNegative)
                .filter(negative -> negative != null && !negative.isBlank())
                .distinct()
                .toList();
    }

    /**
     * 연관 코스 리스트 생성
     */
    private List<CourseCardResponseDto> buildRelatedCourses(List<Pin> pinList) {
        List<Course> relatedCourses = pinList.stream()
                .map(Pin::getCourse)
                .distinct()
                .limit(10)
                .toList();

        return relatedCourses.stream()
                .map(course -> {
                    List<String> courseImages = course.getPinList().stream()
                            .flatMap(pin -> pin.getPinImages().stream())
                            .map(PinImage::getImageUrl)
                            .limit(3)
                            .toList();
                    return CourseCardResponseDto.from(course, courseImages);
                })
                .toList();
    }

    /**
     * PlaceCardResponseDto 생성
     */
    private PlaceCardResponseDto buildPlaceCard(Place place, List<String> bannerImageList) {
        String placeImageUrl = bannerImageList.isEmpty() ? null : bannerImageList.get(0);
        return PlaceCardResponseDto.from(place, placeImageUrl);
    }
}
