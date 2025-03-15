package kz.yandex.practicum.qa.scooter.domain.metro.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil;
import kz.yandex.practicum.qa.scooter.domain.metro.dto.MetroStation;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MetroStationRestApiClient {

    public static final List<MetroStation> STATIONS = findAll();

    static {
        RestAssured.baseURI = ScooterRentUrlUtil.BASE_URL;
    }

    @Step("get metro stations")
    public static List<MetroStation> findAll() {
        MetroStation[] metroStations = RestAssured.given()
                .when()
                .get(ScooterRentUrlUtil.BASE_URL + "/stations/search")
                .then()
                .log()
                .all()
                .extract()
                .response()
                .then()
                .extract()
                .as(MetroStation[].class);

        return List.of(metroStations);
    }

    public static MetroStation random() {
        return STATIONS.get((int) (Math.random() * STATIONS.size()));
    }

}
