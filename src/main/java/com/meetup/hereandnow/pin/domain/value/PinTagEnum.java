package com.meetup.hereandnow.pin.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PinTagEnum {

    COZY("편안함"),
    EXCITED("흥미로운");

    private final String name;
}
