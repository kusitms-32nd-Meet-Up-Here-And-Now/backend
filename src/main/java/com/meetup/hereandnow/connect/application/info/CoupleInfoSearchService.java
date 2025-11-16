package com.meetup.hereandnow.connect.application.info;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseBannerResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoResponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleInfoSearchService {

    private final CoupleRepository coupleRepository;
    private final CourseRepository courseRepository;
    private final ObjectStorageService objectStorageService;

    @Transactional
    public CoupleInfoResponseDto getCoupleInfoResponse() {
        Member member = SecurityUtils.getCurrentMember();

        Couple couple = coupleRepository.findByMember(member)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        List<Course> courseList = courseRepository.findByCourseVisitMemberAndMemberIn(
                "연인", List.of(couple.getMember1(), couple.getMember2())
        );

        int placeWithCount = 0;
        int courseWithCount = courseList.size();
        String imageUrl = couple.getCoupleBannerImageUrl() != null
                ? objectStorageService.buildImageUrl(couple.getCoupleBannerImageUrl())
                : null;


        for (Course c : courseList) {
            List<Pin> pinList = c.getPinList();
            placeWithCount += pinList.size();
        }

        return CoupleInfoResponseDto.from(
                couple.getCoupleStartDate(),
                couple,
                placeWithCount,
                courseWithCount,
                imageUrl
        );
    }

    @Transactional
    public Slice<CoupleCourseBannerResponseDto> getBannerResponse(
            int page, int size
    ) {
        Member member = SecurityUtils.getCurrentMember();

        Couple couple = coupleRepository.findByMember(member)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);

        Pageable pageable = PageRequest.of(page, size);

        List<Course> courseList = courseRepository.findByCourseVisitMemberAndMemberIn(
                "연인", List.of(couple.getMember1(), couple.getMember2())
        );

        List<CoupleCourseBannerResponseDto> bannerList = courseList.stream()
                .map(CoupleCourseBannerResponseDto::from)
                .toList();

        int fromIndex = Math.min(page * size, bannerList.size());
        int toIndex = Math.min(fromIndex + size, bannerList.size());
        List<CoupleCourseBannerResponseDto> content = bannerList.subList(fromIndex, toIndex);
        boolean hasNext = toIndex < bannerList.size();

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
