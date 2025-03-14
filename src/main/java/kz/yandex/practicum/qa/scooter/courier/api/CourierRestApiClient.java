package kz.yandex.practicum.qa.scooter.courier.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.courier.dto.Courier;
import kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil.COURIER_LOGIN_PATH;
import static kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil.COURIER_PATH;

public final class CourierRestApiClient {

    public static final String MESSAGE_USERNAME_ALREADY_EXISTS = "Этот логин уже используется. Попробуйте другой.";
    public static final String MESSAGE_INSUFFICIENT_DATA = "Недостаточно данных для создания учетной записи";

    private CourierRestApiClient() {
        baseURI = ScooterRentUrlUtil.BASE_URL;
    }

    @Step("Create courier")
    public static Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Login courier")
    public static Response authenticate(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier.asCredentials())
                .when()
                .post(COURIER_LOGIN_PATH);
    }

    @Step("Delete courier")
    public static Response delete(Long courierId) {
        return given().when().delete(COURIER_PATH + "/" + courierId);
    }
}
