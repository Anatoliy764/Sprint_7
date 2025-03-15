package kz.yandex.practicum.qa.scooter.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.common.Color;
import kz.yandex.practicum.qa.scooter.domain.metro.api.MetroStationRestApiClient;
import kz.yandex.practicum.qa.scooter.domain.order.api.OrderRestApiClient;
import kz.yandex.practicum.qa.scooter.domain.order.dto.Order;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
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
            .setMetroStation(MetroStationRestApiClient.random().getNumber())
            .setPhone(FAKER.phoneNumber().cellPhone().replaceAll("[^0-9]", ""))
            .setRentTime(FAKER.number().numberBetween(1, 7))
            .setDeliveryDate(LocalDate.now().plusDays(FAKER.number().numberBetween(1, 7)))
            .setComment(FAKER.hobbit().quote());

    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters() {
        return List.of(new Object[][]{
                {null, true}, // No color
                {List.of(Color.BLACK), true},
                {List.of(Color.BLACK, Color.GREY), true},
                {List.of(Color.GREY), true},
                {List.of(Color.GREEN), false} // Не валидный параметр, ожидается ошибка
        });
    }

    private final List<Color> colors;
    private final boolean isSuccessExpected;

    public OrderCreateTest(List<Color> colors, boolean isSuccessExpected) {
        this.colors = colors;
        this.isSuccessExpected = isSuccessExpected;
    }

    @AfterClass
    public static void tearDownAfterClass() {
        for (Long orderId : CREATED_ORDERS_IDS) {
            // Ответ не интересует, т.к. мы не тестируем создание, а не отмену.
            // Наше дело отправить запрос на отмену для удаления заказа после тестов создания.
            OrderRestApiClient.cancel(orderId);
        }
    }

    @Test
    @DisplayName("Тест создания заказа")
    @Description("Параметризованный тест создания заказа с различными комбинациями значений в поле color")
    public void testCreateOrder() {
        if (colors != null) {
            ORDER.setColor(colors);
        }

        int expectedStatusCode = isSuccessExpected ? HttpStatus.SC_CREATED : HttpStatus.SC_BAD_REQUEST;

        long trackNumber = OrderRestApiClient.create(ORDER)
                .then().assertThat().statusCode(expectedStatusCode).and()
                .body(OrderRestApiClient.TRACK_JSON_PATH, isSuccessExpected ? notNullValue() : null)
                .extract().response().getBody().jsonPath().getLong(OrderRestApiClient.TRACK_JSON_PATH);

        if (isSuccessExpected) {
            CREATED_ORDERS_IDS.add(trackNumber);
        }
    }
}
