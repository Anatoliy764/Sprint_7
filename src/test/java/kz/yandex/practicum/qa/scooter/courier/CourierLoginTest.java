package kz.yandex.practicum.qa.scooter.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.scooter.courier.dto.Courier;
import kz.yandex.practicum.qa.scooter.courier.dto.Credentials;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
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

        given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS)
                .when()
                .post(COURIER_PATH)
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @AfterClass
    public static void tearDownAfterClass() {
        given().when().delete(COURIER_PATH + "/" + ((Courier) CREDENTIALS).getId());
    }

    // 1. курьер может авторизоваться;
    // 6. успешный запрос возвращает id.

    @Test
    @DisplayName("Тест аутентификации курьера должен вернуть ok")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 200")
    public void testLoginShouldRespondOk() {

        Long courierId = given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS)
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
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

        given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS.clone().setLogin(null))
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без пароля должен вернуть bad request")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"message\":  \"Недостаточно данных для входа\"}")
    public void testLoginWithoutPasswordShouldRespondBadRequest() {

        given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS.clone().setPassword(null))
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без логина и пароля должен вернуть bad request")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"message\":  \"Недостаточно данных для входа\"}")
    public void testLoginWithoutUsernameAndPasswordShouldRespondBadRequest() {

        given()
                .header("Content-type", "application/json")
                .body(new Credentials())
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // 3. система вернёт ошибку, если неправильно указать логин или пароль;
    // 5. если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;

    @Test
    @DisplayName("Тест аутентификации курьера без логина должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongUsernameShouldRespondBadRequest() {

        given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS.clone().setLogin(FAKER.name().username()))
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без пароля должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongPasswordShouldRespondBadRequest() {

        given()
                .header("Content-type", "application/json")
                .body(CREDENTIALS.clone().setPassword(FAKER.internet().password()))
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест аутентификации курьера без логина и пароля должен вернуть not found")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 404, тело ответа = {\"message\":  \"Учетная запись не найдена\"}")
    public void testLoginWrongUsernameAndPasswordShouldRespondBadRequest() {

        given()
                .header("Content-type", "application/json")
                .body(new Credentials()
                        .setLogin(FAKER.name().username())
                        .setPassword(FAKER.internet().password()))
                .when()
                .post(COURIER_LOGIN_PATH)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
