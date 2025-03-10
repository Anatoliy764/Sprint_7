package kz.yandex.practicum.qa.scooter.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.Order;
import kz.yandex.practicum.qa.scooter.OrderedRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.scooter.util.JsonUtil.toJson;
import static org.hamcrest.Matchers.equalTo;

@RunWith(OrderedRunner.class)
public class CreateCourierTest {

    private static final String COURIER_PATH = "/api/v1/courier";

    private static final Courier COURIER = new Courier()
            .setLogin(FAKER.name().username())
            .setPassword(FAKER.internet().password())
            .setFirstName(FAKER.name().firstName());

    @BeforeClass
    public static void setUpBeforeClass() {
        baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Order
    @DisplayName("Тест создания курьера должен вернуть ok")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 201, тело ответа = {\"ok\":true}")
    public void testCreateCourierShouldReturnOk() {

        String courierJson = toJson(COURIER);

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Тест создания курьера идентичного ранее созданному")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 409, тело ответа = {\"ok\":true}")
    public void testCreateSameCourierShouldReturnConflict() {

        String courierJson = toJson(COURIER);

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body("code", equalTo(409))
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    // если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Тест создания курьера без пароля")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutPasswordShouldReturnBadRequest() {

        String courierJson = toJson(new Courier()
                .setLogin(FAKER.name().username())
                .setFirstName(FAKER.name().firstName()));

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("code", equalTo(400))
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Тест создания курьера без логина")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutLoginShouldReturnBadRequest() {

        String courierJson = toJson(new Courier()
                .setPassword(FAKER.internet().password())
                .setFirstName(FAKER.name().firstName()));

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("code", equalTo(400))
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // чтобы создать курьера, нужно передать в ручку все обязательные поля;
    // система создает курьера только если тело запроса содержит login и password,
    // однако в документации не сказано какие именно поля являются обязательными.
    @Test
    @DisplayName("Тест создания курьера без логина и пароля")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutLoginAndPasswordShouldReturnBadRequest() {

        String courierJson = toJson(new Courier()
                .setFirstName(FAKER.name().firstName()));

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("code", equalTo(400))
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    // чтобы создать курьера, нужно передать в ручку все обязательные поля;
    @Test
    @DisplayName("Тест создания курьера без имени")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 201, тело ответа = {\"ok\":true}")
    public void testCreateCourierWithoutFirstnameShouldReturnOk() {

        String courierJson = toJson(new Courier()
                .setLogin(FAKER.name().username())
                .setPassword(FAKER.internet().password()));

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    // если создать пользователя с логином, который уже есть, возвращается ошибка.
    @Test
    @DisplayName("Тест создания курьера с существующим логином")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":409,\"message\":\"Этот логин уже используется. Попробуйте другой.\"}")
    public void testCreateCourierWithExistentLoginShouldReturnConflict() {

        String courierJson = toJson(COURIER.clone()
                .setPassword(FAKER.internet().password())
                .setFirstName(FAKER.name().firstName()));

        given()
                .header("Content-type", "application/json")
                .body(courierJson)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(409)
                .and()
                .body("code", equalTo(409))
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

}
