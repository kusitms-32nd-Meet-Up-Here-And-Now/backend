package com.meetup.hereandnow.integration.fixture.place;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Random;

public class PlaceEntityFixture {

    private static final Random RANDOM = new Random();
    private static final double MIN_LATITUDE = 37.4;
    private static final double MAX_LATITUDE = 37.7;
    private static final double MIN_LONGITUDE = 126.8;
    private static final double MAX_LONGITUDE = 127.2;

    public static Place getPlace(PlaceGroup placeGroup) {
        WKTReader wktReader = new WKTReader();
        Point location = null;
        try {
            double latitude = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * RANDOM.nextDouble();
            double longitude = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * RANDOM.nextDouble();
            String pointWkt = String.format("POINT(%.6f %.6f)", longitude, latitude);
            location = (Point) wktReader.read(pointWkt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Place.builder()
                .placeName("테스트 장소")
                .placeStreetNameAddress("테스트 도로명 주소")
                .placeNumberAddress("테스트 지번 주소")
                .location(location)
                .placeGroup(placeGroup)
                .placeCategory("카페,디저트")
                .placeUrl("https://test.com")
                .build();
    }

    public static Place getPlace(PlaceGroup placeGroup, double latitude, double longitude) {
        WKTReader wktReader = new WKTReader();
        Point location;
        try {
            String pointWkt = String.format("POINT(%.6f %.6f)", longitude, latitude);
            location = (Point) wktReader.read(pointWkt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return Place.builder()
                .placeName("테스트 장소")
                .placeStreetNameAddress("테스트 도로명 주소")
                .placeNumberAddress("테스트 지번 주소")
                .location(location)
                .placeGroup(placeGroup)
                .placeCategory("카페,디저트")
                .placeUrl("https://test.com")
                .build();
    }

    public static PlaceGroup getFoodPlaceGroup() {
        // TagInitializer에 의해 생성되는 "음식점" PlaceGroup을 가정
        return PlaceGroup.builder()
                .code("FD6")
                .name("음식점")
                .build();
    }
}
