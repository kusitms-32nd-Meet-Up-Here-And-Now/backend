package com.meetup.hereandnow.integration;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.fixture.course.CourseCommentFixture;
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.fixture.pin.PinEntityFixture;
import com.meetup.hereandnow.integration.fixture.pin.PinImageFixture;
import com.meetup.hereandnow.integration.fixture.pin.PinTagFixture;
import com.meetup.hereandnow.integration.fixture.place.PlaceEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.entity.TagValue;
import com.meetup.hereandnow.tag.domain.value.TagGroup;
import com.meetup.hereandnow.tag.infrastructure.repository.PlaceGroupRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import com.meetup.hereandnow.tag.infrastructure.repository.TagValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class FixtureIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private CourseCommentRepository courseCommentRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagValueRepository tagValueRepository;
    @Autowired
    private PlaceGroupRepository placeGroupRepository;

    @BeforeEach
    void setupTestData() {
        if (placeGroupRepository.findByCode("FD6").isEmpty()) {
            PlaceGroup fd6 = placeGroupRepository.save(PlaceGroup.builder().code("FD6").name("음식점").build());
            TagValue tagValue = tagValueRepository.save(TagValue.builder().name("음식이 맛있어요").build());
            tagRepository.save(Tag.builder().placeGroup(fd6).tagValue(tagValue).tagGroup(TagGroup.FOOD_PRICE).build());
        }
    }

    @Test
    @DisplayName("Fixture로 생성한 엔티티들이 DB에 정상적으로 저장된다.")
    void success_fixture_create() {
        // 1. TagInitializer로 생성된 데이터 조회
        PlaceGroup foodPlaceGroup = placeGroupRepository.findByCode("FD6")
                .orElseThrow(() -> new IllegalStateException("PlaceGroup 'FD6'를 찾을 수 없습니다."));

        // findAll()로 전체 태그를 가져온 후, 음식점 PlaceGroup에 속하는 태그 중 하나를 선택
        Tag foodTag = tagRepository.findAll().stream()
                .filter(tag -> tag.getPlaceGroup().getId().equals(foodPlaceGroup.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("음식점(FD6) 관련 태그를 찾을 수 없습니다."));


        // 2. Fixture를 사용하여 엔티티 생성
        Member member = MemberEntityFixture.getMember();
        Course course = CourseEntityFixture.getCourse(member);
        Place place = PlaceEntityFixture.getPlace(foodPlaceGroup);
        Pin pin = PinEntityFixture.getPin(course, place);
        CourseComment courseComment = CourseCommentFixture.getCourseComment(course, member);
        PinImage pinImage = PinImageFixture.getPinImage(pin);
        PinTag pinTag = PinTagFixture.getPinTag(pin, foodTag);

        // 3. Repository를 통해 엔티티 저장 (연관관계 주인을 통해 저장)
        memberRepository.save(member);
        courseRepository.save(course);
        placeRepository.save(place);

        pin.addPinTag(pinTag);
        pin.addPinImage(pinImage);
        pinRepository.save(pin);

        courseCommentRepository.save(courseComment);


        // 4. 저장된 데이터 조회 및 검증
        Member savedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(savedMember.getNickname()).isEqualTo(member.getNickname());

        Course savedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(savedCourse.getCourseTitle()).isEqualTo(course.getCourseTitle());

        Place savedPlace = placeRepository.findById(place.getId()).orElseThrow();
        assertThat(savedPlace.getPlaceName()).isEqualTo(place.getPlaceName());
        assertThat(savedPlace.getPlaceGroup().getCode()).isEqualTo("FD6");

        Pin savedPin = pinRepository.findById(pin.getId()).orElseThrow();
        assertThat(savedPin.getPinPositive()).isEqualTo(pin.getPinPositive());

        CourseComment savedCourseComment = courseCommentRepository.findById(courseComment.getId()).orElseThrow();
        assertThat(savedCourseComment.getContent()).isEqualTo(courseComment.getContent());

        // Pin에 Cascade 설정이 되어있으므로 Pin을 통해 조회
        PinImage savedPinImage = savedPin.getPinImages().get(0);
        assertThat(savedPinImage.getImageUrl()).isEqualTo(pinImage.getImageUrl());

        PinTag savedPinTag = savedPin.getPinTags().get(0);
        assertThat(savedPinTag.getTag().getId()).isEqualTo(foodTag.getId());
        assertThat(savedPinTag.getTag().getPlaceGroup().getCode()).isEqualTo("FD6");
    }
}
