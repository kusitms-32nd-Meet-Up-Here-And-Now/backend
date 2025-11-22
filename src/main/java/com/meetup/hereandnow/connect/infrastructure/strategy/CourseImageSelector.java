package com.meetup.hereandnow.connect.infrastructure.strategy;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseImageSelector {

    private final ObjectStorageService objectStorageService;

    private static final int MAX_IMAGE_COUNT = 3;

    public List<String> selectRandomImages(Course course) {
        List<PinImage> allImages = course.getPinList().stream()
                .flatMap(pin -> pin.getPinImages().stream())
                .toList();

        if (allImages.isEmpty()) {
            return Collections.emptyList();
        }

        List<PinImage> shuffledImages = new java.util.ArrayList<>(allImages);
        Collections.shuffle(shuffledImages);

        return shuffledImages.stream()
                .map(pinImage -> objectStorageService.buildImageUrl(pinImage.getImageUrl()))
                .limit(MAX_IMAGE_COUNT)
                .toList();
    }

    public String selectFirstImage(Course course) {
        return course.getPinList().stream()
                .flatMap(pin -> pin.getPinImages().stream())
                .findFirst()
                .map(pinImage -> objectStorageService.buildImageUrl(pinImage.getImageUrl()))
                .orElse(null);
    }
}

