package com.meetup.hereandnow.place.infrastructure.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class PlaceKeyFactoryTest {

    private PlaceKeyFactory placeKeyFactory;

    @BeforeEach
    void setUp() {
        placeKeyFactory = new PlaceKeyFactory();
    }

    @Test
    @DisplayName("장소 키 생성에 성공한다.")
    void success_place_key() {
        //given
        String name = "테스트 장소";
        double lat = 37.5;
        double lon = 127.123;

        // when
        String result = placeKeyFactory.buildKey(name, lat, lon);

        // then
        assertThat(result).isEqualTo("테스트 장소|37.5|127.123");
    }
}