package com.meetup.hereandnow.integration.fixture.pin;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;

public class PinImageFixture {

    public static PinImage getPinImage(Pin pin) {
        return PinImage.builder()
                .imageUrl("https://example.com/image.jpg")
                .pin(pin)
                .build();
    }
}
