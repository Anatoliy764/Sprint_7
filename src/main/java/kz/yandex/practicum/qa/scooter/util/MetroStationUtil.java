package kz.yandex.practicum.qa.scooter.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.order.MetroStation;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class MetroStationUtil {

    public static final List<MetroStation> STATIONS = findAll();

    public static List<MetroStation> findAll() {
        Response response = RestAssured.given()
                .when()
                .get("https://qa-scooter.praktikum-services.ru/api/v1/stations/search")
                .thenReturn();

        String json = response.getBody().prettyPrint();

        return Arrays.asList(JsonUtil.fromJson(json, MetroStation[].class));
    }

    public static MetroStation random() {
        return STATIONS.get((int) (Math.random() * STATIONS.size()));
    }

}
