package kz.yandex.practicum.qa.scooter.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.util.JsonUtil;
import kz.yandex.practicum.qa.scooter.util.MetroStationUtil;
import kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.scooter.util.JsonUtil.*;
import static kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil.ORDER_PATH;
import static org.hamcrest.Matchers.*;

/*
* 4. Список заказов
* 1. Проверь, что в тело ответа возвращается список заказов.
* */
public class OrderListTest {

    private static final Order ORDER = new Order();

    @BeforeClass
    public static void setUpBeforeClass() {
        baseURI = ScooterRentUrlUtil.BASE_URL;

        ORDER.setFirstName(FAKER.name().firstName())
                .setLastName(FAKER.name().lastName())
                .setAddress(FAKER.address().streetAddress())
                .setMetroStation(MetroStationUtil.random().getNumber())
                .setPhone(FAKER.phoneNumber().cellPhone().replaceAll("[^0-9]", ""))
                .setRentTime(FAKER.number().numberBetween(1, 7))
                .setDeliveryDate(LocalDate.now().plusDays(FAKER.number().numberBetween(1, 7)))
                .setComment(FAKER.hobbit().quote());

        long orderId = given().header("Content-type", "application/json").body(toJson(ORDER)).post(ORDER_PATH)
                .getBody().jsonPath().getLong("track");

        System.out.println("Order ID: " + orderId);

        ORDER.setId(orderId);
    }

    @Test
    @DisplayName("Тест получения списка заказов")
    public void testGetOrdersShouldReturnArray() {
        given()
                .when().get(ScooterRentUrlUtil.ORDER_PATH)
                .then().assertThat()
                .statusCode(200).and()
                .body("orders", notNullValue()).and()
                .body("orders", instanceOf(List.class)).and()
                .body("orders", hasSize(greaterThan(0)));
    }

    @AfterClass
    public static void tearDownAfterClass() {
        given().delete(ORDER_PATH + "/" + ORDER.getId());
    }
}
