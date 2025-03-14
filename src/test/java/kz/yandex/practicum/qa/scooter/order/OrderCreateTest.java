package kz.yandex.practicum.qa.scooter.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.util.MetroStationUtil;
import kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil.*;
import static org.hamcrest.Matchers.notNullValue;

/*
* 3. Создание заказа
* Проверь, что когда создаёшь заказ:
* 1. можно указать один из цветов — BLACK или GREY;
* 2. можно указать оба цвета;
* 3. можно совсем не указывать цвет;
* 4. тело ответа содержит track.
* */
@RunWith(Parameterized.class)
public class OrderCreateTest {

    private static final List<Long> CREATED_ORDERS_IDS = new LinkedList<>();

    private static final Order ORDER = new Order()
            .setFirstName(FAKER.name().firstName())
            .setLastName(FAKER.name().lastName())
            .setAddress(FAKER.address().streetAddress())
            .setMetroStation(MetroStationUtil.random().getNumber())
            .setPhone(FAKER.phoneNumber().cellPhone().replaceAll("[^0-9]", ""))
            .setRentTime(FAKER.number().numberBetween(1, 7))
            .setDeliveryDate(LocalDate.now().plusDays(FAKER.number().numberBetween(1, 7)))
            .setComment(FAKER.hobbit().quote());

    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters() {
        return Arrays.asList(new Object[][]{
                {null, true}, // No color
                {Arrays.asList(Color.BLACK), true},
                {Arrays.asList(Color.BLACK, Color.GREY), true},
                {Arrays.asList(Color.GREY), true},
                {Arrays.asList(Color.GREEN), false} // Не валидный параметр, ожидается ошибка
        });
    }

    private final List<Color> colors;
    private final boolean isSuccessExpected;

    public OrderCreateTest(List<Color> colors, boolean isSuccessExpected) {
        this.colors = colors;
        this.isSuccessExpected = isSuccessExpected;
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        baseURI = ScooterRentUrlUtil.BASE_URL;
    }

    @AfterClass
    public static void tearDownAfterClass() {
        for (Long orderId : CREATED_ORDERS_IDS) {
            given()
                    .when()
                    .put(ORDER_CANCEL_PATH + orderId)
                    .then().assertThat().statusCode(200);
        }
    }

    @Test
    @DisplayName("Тест создания заказа")
    @Description("Параметризированный тест создания заказа с различными комбинациями значений в поле color")
    public void testCreateOrder() {
        if (colors != null) {
            ORDER.setColor(colors);
        }

        int expectedStatusCode = isSuccessExpected ? 201 : 400;

        long trackNumber = given().header("Content-type", "application/json").body(ORDER)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(expectedStatusCode)
                .body("track", isSuccessExpected ? notNullValue() : null)
                .extract().response().getBody().jsonPath().getLong("track");

        if (isSuccessExpected) {
            CREATED_ORDERS_IDS.add(trackNumber);
        }
    }
}
