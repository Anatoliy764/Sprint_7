package kz.yandex.practicum.qa.scooter.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.scooter.util.JsonUtil.toJson;
import static kz.yandex.practicum.qa.scooter.util.ScooterRentUrlUtil.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/*
 * 1. Логин курьера
 * Проверь:
 * 1. курьер может авторизоваться;
 * 2. для авторизации нужно передать все обязательные поля;
 * 3. система вернёт ошибку, если неправильно указать логин или пароль;
 * 4. если какого-то поля нет, запрос возвращает ошибку;
 * 5. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;
 * 6. успешный запрос возвращает id.
 * */
public class CourierLoginTest {

    private static final Credentials CREDENTIALS = new Courier()
            .setLogin(FAKER.name().username())
            .setPassword(FAKER.internet().password());

    @BeforeClass
    public static void setUpBeforeClass() {
        baseURI = BASE_URL;

        String courierJson = toJson(CREDENTIALS);

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

    // 1. курьер может авторизоваться;
    // 6. успешный запрос возвращает id.

    @Test
    @DisplayName("Тест аутентификации курьера должен вернуть ok")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 200")
    public void testLoginShouldRespondOk() {

        String credentialsJson = toJson(CREDENTIALS);

        System.out.println(credentialsJson);

        Long courierId = given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", notNullValue())
                .extract().response().jsonPath().getLong("id");

        ((Courier) CREDENTIALS).setId(courierId);
    }

    // 2. для авторизации нужно передать все обязательные поля;
    // 4. если какого-то поля нет, запрос возвращает ошибку;

    @Test
    @DisplayName("Тест аутентификации курьера без логина должен вернуть bad request")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"message\":  \"Недостаточно данных для входа\"}")
    public void testLoginWithoutUsernameShouldRespondBadRequest() {

        String credentialsJson = toJson(CREDENTIALS.clone().setLogin(null));

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без пароля должен вернуть bad request")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"message\":  \"Недостаточно данных для входа\"}")
    public void testLoginWithoutPasswordShouldRespondBadRequest() {

        String credentialsJson = toJson(CREDENTIALS.clone().setPassword(null));

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без логина и пароля должен вернуть bad request")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"message\":  \"Недостаточно данных для входа\"}")
    public void testLoginWithoutUsernameAndPasswordShouldRespondBadRequest() {

        String credentialsJson = toJson(new Credentials());

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // 3. система вернёт ошибку, если неправильно указать логин или пароль;
    // 5. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;

    @Test
    @DisplayName("Тест аутентификации курьера без логина должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongUsernameShouldRespondBadRequest() {

        String credentialsJson = toJson(CREDENTIALS.clone().setLogin(FAKER.name().username()));

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без пароля должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongPasswordShouldRespondBadRequest() {

        String credentialsJson = toJson(CREDENTIALS.clone().setPassword(FAKER.internet().password()));

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без логина и пароля должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongUsernameAndPasswordShouldRespondBadRequest() {

        String credentialsJson = toJson(new Credentials()
                .setLogin(FAKER.name().username())
                .setPassword(FAKER.internet().password()));

        System.out.println(credentialsJson);

        given()
                .header("Content-type", "application/json")
                .body(credentialsJson)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @AfterClass
    public static void tearDownAfterClass() {
        given().when().delete(COURIER_PATH + "/" + ((Courier) CREDENTIALS).getId());
    }
}
