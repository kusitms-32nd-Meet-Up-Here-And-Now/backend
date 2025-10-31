package com.meetup.hereandnow.place.dto;

import com.meetup.hereandnow.pin.domain.value.PinTagEnum;

public record PlaceTagDto(
        Long placeId,
        PinTagEnum tagEnum,
        Long tagCount
) {
}
