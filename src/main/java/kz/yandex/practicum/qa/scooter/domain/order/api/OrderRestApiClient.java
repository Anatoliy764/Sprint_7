package kz.yandex.practicum.qa.scooter.domain.order.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil;
import kz.yandex.practicum.qa.scooter.domain.order.dto.Order;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil.ORDER_CANCEL_PATH;
import static kz.yandex.practicum.qa.scooter.common.util.ScooterRentUrlUtil.ORDER_PATH;

@UtilityClass
public class OrderRestApiClient {

    static {
        RestAssured.baseURI = ScooterRentUrlUtil.BASE_URL;
    }

    @Step("Create order")
    public static Response create(Order order) {
        return given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(order)
                .post(ORDER_PATH);
    }

    @Step("Get orders")
    public static Response get() {
        return given().get(ScooterRentUrlUtil.ORDER_PATH);
    }

    @Step("Cancel order")
    public static Response cancel(Long trackId) {
        return given().put(ORDER_CANCEL_PATH + trackId);
    }
}
