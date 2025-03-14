package kz.yandex.practicum.qa.scooter.util;

import io.restassured.RestAssured;
import kz.yandex.practicum.qa.scooter.order.MetroStation;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class MetroStationUtil {

    public static final List<MetroStation> STATIONS = findAll();

    public static List<MetroStation> findAll() {
        MetroStation[] metroStations = RestAssured.given()
                .when()
                .get(ScooterRentUrlUtil.BASE_URL + "/stations/search")
                .then().extract().as(MetroStation[].class);

        return Arrays.asList(metroStations);
    }

    public static MetroStation random() {
        return STATIONS.get((int) (Math.random() * STATIONS.size()));
    }

}
