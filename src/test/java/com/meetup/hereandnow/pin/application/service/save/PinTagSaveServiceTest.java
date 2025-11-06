package com.meetup.hereandnow.pin.application.service.save;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.entity.TagValue;
import com.meetup.hereandnow.tag.infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PinTagSaveServiceTest {

    @Mock
    private PinTagRepository pinTagRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PinTagSaveService pinTagSaveService;

    private Pin pin1, pin2;
    private PinSaveDto pinDto1, pinDto2;
    private Tag tagA_CT1, tagB_CT1, tagC_FD6;
    private final MockedStatic<PinTag> mockedPinTag = mockStatic(PinTag.class);

    @BeforeEach
    void setUp() {

        pin1 = mock(Pin.class);
        pin2 = mock(Pin.class);

        pinDto1 = mock(PinSaveDto.class);
        pinDto2 = mock(PinSaveDto.class);
        PlaceSaveDto placeDto1 = mock(PlaceSaveDto.class);
        PlaceSaveDto placeDto2 = mock(PlaceSaveDto.class);

        given(placeDto1.placeGroupCode()).willReturn("CT1");
        given(pinDto1.place()).willReturn(placeDto1);
        given(pinDto1.pinTagNames()).willReturn(List.of("태그A", "태그B"));

        given(placeDto2.placeGroupCode()).willReturn("FD6");
        given(pinDto2.place()).willReturn(placeDto2);
        given(pinDto2.pinTagNames()).willReturn(List.of("태그C"));

        tagA_CT1 = mockTag("CT1", "태그A");
        tagB_CT1 = mockTag("CT1", "태그B");
        tagC_FD6 = mockTag("FD6", "태그C");
    }

    private Tag mockTag(String groupCode, String tagName) {
        Tag tag = mock(Tag.class);
        PlaceGroup pg = mock(PlaceGroup.class);
        TagValue tv = mock(TagValue.class);

        given(tag.getPlaceGroup()).willReturn(pg);
        given(tag.getTagValue()).willReturn(tv);
        given(pg.getCode()).willReturn(groupCode);
        given(tv.getName()).willReturn(tagName);

        return tag;
    }

    @Test
    @DisplayName("여러 DTO의 태그로 PinTag를 올바르게 생성하고 저장한다")
    void save_pin_tags() {

        // given
        List<Pin> pins = List.of(pin1, pin2);
        List<PinSaveDto> dtos = List.of(pinDto1, pinDto2);

        Set<String> ct1Tags = Set.of("태그A", "태그B");
        Set<String> fd6Tags = Set.of("태그C");

        given(tagRepository.findByPlaceGroupCodeAndTagNames("CT1", ct1Tags))
                .willReturn(List.of(tagA_CT1, tagB_CT1));
        given(tagRepository.findByPlaceGroupCodeAndTagNames("FD6", fd6Tags))
                .willReturn(List.of(tagC_FD6));

        PinTag pinTag1_A = mock(PinTag.class);
        PinTag pinTag1_B = mock(PinTag.class);
        PinTag pinTag2_C = mock(PinTag.class);

        mockedPinTag.when(() -> PinTag.of(tagA_CT1, pin1)).thenReturn(pinTag1_A);
        mockedPinTag.when(() -> PinTag.of(tagB_CT1, pin1)).thenReturn(pinTag1_B);
        mockedPinTag.when(() -> PinTag.of(tagC_FD6, pin2)).thenReturn(pinTag2_C);

        // when
        pinTagSaveService.savePinTags(pins, dtos);

        // then
        verify(tagRepository).findByPlaceGroupCodeAndTagNames("CT1", ct1Tags);
        verify(tagRepository).findByPlaceGroupCodeAndTagNames("FD6", fd6Tags);
        verify(pinTagRepository).saveAll(List.of(
                PinTag.of(tagA_CT1, pin1),
                PinTag.of(tagB_CT1, pin1),
                PinTag.of(tagC_FD6, pin2)
        ));
    }
}