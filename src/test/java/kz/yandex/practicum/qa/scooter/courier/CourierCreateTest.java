package kz.yandex.practicum.qa.scooter.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.First;
import kz.yandex.practicum.qa.scooter.OrderedRunner;
import kz.yandex.practicum.qa.scooter.courier.api.CourierRestApiClient;
import kz.yandex.practicum.qa.scooter.courier.dto.Courier;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static kz.yandex.practicum.qa.scooter.FakerInstance.FAKER;
import static org.hamcrest.Matchers.equalTo;

/*
* 1. Создание курьера
* Проверь:
* 1. курьера можно создать;
* 2. нельзя создать двух одинаковых курьеров;
* 3. чтобы создать курьера, нужно передать в ручку все обязательные поля;
* 4. запрос возвращает правильный код ответа;
* 5. успешный запрос возвращает ok: true;
* 6. если одного из полей нет, запрос возвращает ошибку;
* 7. если создать пользователя с логином, который уже есть, возвращается ошибка.
* */
public class CourierCreateTest {

    private static final List<Courier> COURIERS = new LinkedList<>();

    private static Courier courierFullyInitialized;
    private static Courier courierDuplicated;
    private static Courier courierWithoutPassword;
    private static Courier courierWithoutUsername;
    private static Courier courierWithoutUsernameAndPassword;
    private static Courier courierWithUsernameAndPassword;
    private static Courier courierWithExistentUsername;

    @BeforeClass
    public static void setUpBeforeClass() {
        courierFullyInitialized = new Courier()
                .setLogin(FAKER.name().username())
                .setPassword(FAKER.internet().password())
                .setFirstName(FAKER.name().firstName());

        courierDuplicated = courierFullyInitialized.clone();

        courierWithoutPassword = new Courier().setLogin(FAKER.name().username());

        courierWithoutUsername = new Courier().setPassword(FAKER.internet().password());

        courierWithoutUsernameAndPassword = new Courier().setFirstName(FAKER.name().firstName());

        courierWithUsernameAndPassword = new Courier()
                .setLogin(FAKER.name().username())
                .setPassword(FAKER.internet().password());

        courierWithExistentUsername = courierWithUsernameAndPassword.clone()
                .setPassword(FAKER.internet().password());

        COURIERS.add(courierFullyInitialized);
        COURIERS.add(courierWithoutPassword);
        COURIERS.add(courierWithoutUsername);
        COURIERS.add(courierWithoutUsernameAndPassword);
        COURIERS.add(courierWithUsernameAndPassword);
        COURIERS.add(courierWithExistentUsername);
    }

    @AfterClass
    public static void tearDownAfterClass() {

        for (Courier courier : COURIERS) {
            // Аутентифицируем курьера для получения идентификатора необходимого для удаления.
            // API не удачно сделано, т.к. приходится в тесте создания курьеров неявно тестировать аутентификацию,
            // по хорошему при создании курьера нужно возвращать идентификатор созданного клиента.
            Response response = CourierRestApiClient.authenticate(courier);

            if(response.getStatusCode() == HttpStatus.SC_OK) {
                long courierId = response.then().extract().jsonPath().getLong("id");
                // удаляем курьера если он был создан и успешно аутентифицирован
                // неявно тестируется удаление в тесте, который по хорошему должен только проверять создание.
                // было бы лучше сделать тесты зависимыми, либо же сделать их последовательными с общим контекстом,
                // тогда после создания, можно проверить авторизацию и взять ранее созданных курьеров из контекста,
                // затем точно так же удалить курьеров.
                CourierRestApiClient.delete(courierId);
            }
        }
    }

    // * 1. курьера можно создать;
    // * 3. чтобы создать курьера, нужно передать в ручку все обязательные поля;
    // * 4. запрос возвращает правильный код ответа;
    // * 5. успешный запрос возвращает ok: true;
    @Test
    @DisplayName("Тест создания курьера должен вернуть ok")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 201, тело ответа = {\"ok\":true}")
    public void testCreateCourierShouldReturnOk() {

        CourierRestApiClient.create(courierFullyInitialized)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", equalTo(true));

    }

    
    // * 2. нельзя создать двух одинаковых курьеров;
    @Test
    @DisplayName("Тест создания курьера идентичного ранее созданному")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 409, тело ответа = {\"ok\":true}")
    public void testCreateSameCourierShouldReturnConflict() {

        CourierRestApiClient.create(courierDuplicated)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("code", equalTo(HttpStatus.SC_CONFLICT))
                .and()
                .body("message", equalTo(CourierRestApiClient.MESSAGE_USERNAME_ALREADY_EXISTS));
    }

    // * 6. если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Тест создания курьера без пароля")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutPasswordShouldReturnBadRequest() {

        CourierRestApiClient.create(courierWithoutPassword)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("code", equalTo(HttpStatus.SC_BAD_REQUEST))
                .and()
                .body("message", equalTo(CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA));
    }

    // * 6. если одного из полей нет, запрос возвращает ошибку;
    @Test
    @DisplayName("Тест создания курьера без логина")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutLoginShouldReturnBadRequest() {

        CourierRestApiClient.create(courierWithoutUsername)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("code", equalTo(HttpStatus.SC_BAD_REQUEST))
                .and()
                .body("message", equalTo(CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA));
    }

    // * 6. если одного из полей нет, запрос возвращает ошибку;
    // чтобы создать курьера, нужно передать в ручку все обязательные поля;
    // система создает курьера только если тело запроса содержит login и password,
    // однако в документации не сказано какие именно поля являются обязательными.
    @Test
    @DisplayName("Тест создания курьера без логина и пароля")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":400,\"message\":\"Недостаточно данных для создания учетной записи.\"}")
    public void testCreateCourierWithoutLoginAndPasswordShouldReturnBadRequest() {

        CourierRestApiClient.create(courierWithoutUsernameAndPassword)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("code", equalTo(HttpStatus.SC_BAD_REQUEST))
                .and()
                .body("message", equalTo(CourierRestApiClient.MESSAGE_INSUFFICIENT_DATA));
    }

    // чтобы создать курьера, нужно передать в ручку все обязательные поля;
    @Test
    @DisplayName("Тест создания курьера без имени")
    @Description("Ответ должен быть успешным. Ожидаемый статус = 201, тело ответа = {\"ok\":true}")
    public void testCreateCourierWithoutFirstnameShouldReturnOk() {

        CourierRestApiClient.create(courierWithUsernameAndPassword)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", equalTo(true));
    }

    // если создать пользователя с логином, который уже есть, возвращается ошибка.
    @Test
    @DisplayName("Тест создания курьера с существующим логином")
    @Description("Ответ должен быть не успешным. Ожидаемый статус = 400, тело ответа = {\"code\":409,\"message\":\"Этот логин уже используется. Попробуйте другой.\"}")
    public void testCreateCourierWithExistentLoginShouldReturnConflict() {

        CourierRestApiClient.create(courierWithExistentUsername)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("code", equalTo(HttpStatus.SC_CONFLICT))
                .and()
                .body("message", equalTo(CourierRestApiClient.MESSAGE_USERNAME_ALREADY_EXISTS));
    }
}
