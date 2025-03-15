package kz.yandex.practicum.qa.scooter.order;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.domain.metro.api.MetroStationRestApiClient;
import kz.yandex.practicum.qa.scooter.domain.order.api.OrderRestApiClient;
import kz.yandex.practicum.qa.scooter.domain.order.dto.Order;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static org.hamcrest.Matchers.*;

/*
 * 4. Список заказов
 * 1. Проверь, что в тело ответа возвращается список заказов.
 * */
public class OrderListTest {

    private static final Order ORDER = new Order();

    @BeforeClass
    public static void setUpBeforeClass() {

        ORDER.setFirstName(FAKER.name().firstName())
                .setLastName(FAKER.name().lastName())
                .setAddress(FAKER.address().streetAddress())
                .setMetroStation(MetroStationRestApiClient.random().getNumber())
                .setPhone(FAKER.phoneNumber().cellPhone().replaceAll("[^0-9]", ""))
                .setRentTime(FAKER.number().numberBetween(1, 7))
                .setDeliveryDate(LocalDate.now().plusDays(FAKER.number().numberBetween(1, 7)))
                .setComment(FAKER.hobbit().quote());

        long orderId = OrderRestApiClient.create(ORDER)
                .getBody().jsonPath().getLong("track");

        ORDER.setId(orderId);
    }

    @Test
    @DisplayName("Тест получения списка заказов")
    public void testGetOrdersShouldReturnArray() {
        OrderRestApiClient.get()
                .then().assertThat()
                .statusCode(200).and()
                .body("orders", notNullValue()).and()
                .body("orders", instanceOf(List.class)).and()
                .body("orders", hasSize(greaterThan(0)));
    }

    @AfterClass
    public static void tearDownAfterClass() {
        OrderRestApiClient.cancel(ORDER.getId());
    }
}
