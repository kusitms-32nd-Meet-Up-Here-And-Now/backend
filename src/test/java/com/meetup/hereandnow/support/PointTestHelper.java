package com.meetup.hereandnow.support;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Random;

public class PointTestHelper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static final Random random = new Random();

    public static Point randomPoint() {
        double lon = 126.8 + random.nextDouble() * 0.4; // 서울 경도
        double lat = 37.4 + random.nextDouble() * 0.3;  // 서울 위도
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
