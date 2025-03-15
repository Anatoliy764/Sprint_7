package kz.yandex.practicum.qa.scooter.domain.courier.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.domain.courier.dto.Courier;
import kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil;
import kz.yandex.practicum.qa.scooter.domain.courier.dto.Credentials;
import lombok.experimental.UtilityClass;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil.COURIER_LOGIN_PATH;
import static kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil.COURIER_PATH;

@UtilityClass
public class CourierRestApiClient {

    public static final String MESSAGE_USERNAME_ALREADY_EXISTS = "Этот логин уже используется. Попробуйте другой.";
    public static final String MESSAGE_INSUFFICIENT_DATA = "Недостаточно данных для создания учетной записи";
    public static final String MESSAGE_INSUFFICIENT_DATA_FOR_LOGIN = "Недостаточно данных для входа";
    public static final String MESSAGE_ACCOUNT_NOT_FOUND = "Учетная запись не найдена";

    static {
        baseURI = ScooterRentUrlUtil.BASE_URL;
    }

    @Step("Create courier")
    public static Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(COURIER_PATH);
    }

    @Step("Login courier")
    public static Response authenticate(Credentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .post(COURIER_LOGIN_PATH);
    }

    @Step("Delete courier")
    public static Response delete(Long courierId) {
        return given().delete(COURIER_PATH + "/" + courierId);
    }
}
