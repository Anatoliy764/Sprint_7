package kz.yandex.practicum.qa.scooter.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import kz.yandex.practicum.qa.scooter.domain.courier.api.CourierRestApiClient;
import kz.yandex.practicum.qa.scooter.domain.courier.dto.Courier;
import kz.yandex.practicum.qa.scooter.domain.courier.dto.Credentials;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static org.apache.http.HttpStatus.*;
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
@RunWith(Parameterized.class)
public class CourierLoginTest {

    private static final Credentials CREDENTIALS = new Courier()
            .setLogin(FAKER.name().username())
            .setPassword(FAKER.internet().password());

    private final Credentials inputCredentials;
    private final int expectedStatusCode;
    private final String expectedMessage;

    public CourierLoginTest(Credentials inputCredentials, int expectedStatusCode, String expectedMessage) {
        this.inputCredentials = inputCredentials;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedMessage = expectedMessage;
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        CourierRestApiClient.create((Courier) CREDENTIALS)
                .then()
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body(CourierRestApiClient.OK_JSON_PATH, equalTo(true));
    }

    @AfterClass
    public static void tearDownAfterClass() {
        CourierRestApiClient.delete(((Courier) CREDENTIALS).getId());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return List.of(new Object[][]{
                // TestCase 1: Successful login
                {CREDENTIALS, SC_OK, null},

                // TestCase 2: Missing username
                {CREDENTIALS.clone().setLogin(null), SC_BAD_REQUEST, CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA_FOR_LOGIN},

                // TestCase 3: Missing password
                {CREDENTIALS.clone().setPassword(null), SC_BAD_REQUEST, CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA_FOR_LOGIN},

                // TestCase 4: Missing both username and password
                {new Credentials(), SC_BAD_REQUEST, CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA_FOR_LOGIN},

                // TestCase 5: Incorrect username
                {CREDENTIALS.clone().setLogin(FAKER.name().username()), SC_NOT_FOUND, CourierRestApiClient.MESSAGE_ACCOUNT_NOT_FOUND},

                // TestCase 6: Incorrect password
                {CREDENTIALS.clone().setPassword(FAKER.internet().password()), SC_NOT_FOUND, CourierRestApiClient.MESSAGE_ACCOUNT_NOT_FOUND},

                // TestCase 7: Both username and password incorrect
                {new Credentials()
                        .setLogin(FAKER.name().username())
                        .setPassword(FAKER.internet().password()), SC_NOT_FOUND, CourierRestApiClient.MESSAGE_ACCOUNT_NOT_FOUND},
        });
    }

    @Test
    @DisplayName("Тест аутентификации курьера")
    @Description("Параметризованный тест с различными комбинациями логина/пароля")
    public void testCourierLogin() {
        ValidatableResponse response = CourierRestApiClient.authenticate(inputCredentials)
                .then()
                .assertThat()
                .statusCode(expectedStatusCode);

        if (expectedMessage != null) {
            response.body(CourierRestApiClient.MESSAGE_JSON_PATH, equalTo(expectedMessage));
        } else {
            response.body(CourierRestApiClient.ID_JSON_PATH, notNullValue());
        }
    }
}