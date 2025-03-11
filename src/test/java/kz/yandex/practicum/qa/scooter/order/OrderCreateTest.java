package kz.yandex.practicum.qa.scooter.order;

import kz.yandex.practicum.qa.scooter.OrderedRunner;
import kz.yandex.practicum.qa.scooter.util.MetroStationUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.scooter.util.JsonUtil.toJson;
import static org.hamcrest.Matchers.notNullValue;

/*
* 3. Создание заказа
* Проверь, что когда создаёшь заказ:
* 1. можно указать один из цветов — BLACK или GREY;
* 2. можно указать оба цвета;
* 3. можно совсем не указывать цвет;
* 4. тело ответа содержит track.
* */
@RunWith(OrderedRunner.class)
public class OrderCreateTest {

    private static final String ORDER_PATH = "/api/v1/orders";
    private static final String ORDER_CANCEL_PATH = ORDER_PATH + "/cancel";
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

    @BeforeClass
    public static void setUpBeforeClass() {
        baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println(CREATED_ORDERS_IDS);
        for (Long orderId : CREATED_ORDERS_IDS) {
            given()
                    .header("Content-type", "application/json")
                    .body("{\"track\": " + orderId + "}")
                    .when()
                    .put(ORDER_CANCEL_PATH);
        }
    }

    @Test
    @kz.yandex.practicum.qa.scooter.Order(1)
    public void testCreateOrderWithoutColorShouldReturnTrack() {

        String orderJson = toJson(ORDER);

        System.out.println(orderJson);

        long trackNumber = given().header("Content-type", "application/json").body(orderJson)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(201).body("track", notNullValue())
                .extract().response().getBody().jsonPath().getLong("track");

        CREATED_ORDERS_IDS.add(trackNumber);
    }

    @Test
    @kz.yandex.practicum.qa.scooter.Order(2)
    public void testCreateOrderWithBlackColorShouldReturnTrack() {
        String orderJson = toJson(ORDER.addColor(Color.BLACK));

        System.out.println(orderJson);

        long trackNumber = given().header("Content-type", "application/json").body(orderJson)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(201).body("track", notNullValue())
                .extract().response().getBody().jsonPath().getLong("track");

        CREATED_ORDERS_IDS.add(trackNumber);
    }

    @Test
    @kz.yandex.practicum.qa.scooter.Order(3)
    public void testCreateOrderWithBlackAndGreyColorShouldReturnTrack() {
        String orderJson = toJson(ORDER.addColor(Color.GREY));

        System.out.println(orderJson);

        long trackNumber = given().header("Content-type", "application/json").body(orderJson)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(201).body("track", notNullValue())
                .extract().response().getBody().jsonPath().getLong("track");

        CREATED_ORDERS_IDS.add(trackNumber);
    }

    @Test
    @kz.yandex.practicum.qa.scooter.Order(4)
    public void testCreateOrderWithGreyColorShouldReturnTrack() {
        String orderJson = toJson(ORDER.removeColor(Color.BLACK));

        System.out.println(orderJson);

        long trackNumber = given().header("Content-type", "application/json").body(orderJson)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(201).body("track", notNullValue())
                .extract().response().getBody().jsonPath().getLong("track");

        CREATED_ORDERS_IDS.add(trackNumber);
    }

    @Test
    @kz.yandex.practicum.qa.scooter.Order(5)
    public void testCreateOrderWithGreenColorShouldFail() {
        String orderJson = toJson(ORDER.setColor(Arrays.asList(Color.GREEN)));

        System.out.println(orderJson);

        long trackNumber = given().header("Content-type", "application/json").body(orderJson)
                .when().post(ORDER_PATH)
                .then().assertThat().statusCode(201).body("track", notNullValue())
                .extract().response().getBody().jsonPath().getLong("track");

        CREATED_ORDERS_IDS.add(trackNumber);
    }
}
