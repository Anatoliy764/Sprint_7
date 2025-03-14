package kz.yandex.practicum.qa.scooter.courier.api;

import io.restassured.response.Response;
import kz.yandex.practicum.qa.scooter.courier.dto.Courier;
import kz.yandex.practicum.qa.scooter.courier.exceptions.CourierAuthenticationException;
import kz.yandex.practicum.qa.scooter.courier.exceptions.CourierCreateException;
import kz.yandex.practicum.qa.scooter.courier.exceptions.CourierDeleteException;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierService {

    public Courier create(Courier courier) {
        Response response = CourierRestApiClient.create(courier);

        if (response.statusCode() != HttpStatus.SC_CREATED) {
            String errorMessage = response.jsonPath().getString("message");
            throw new CourierCreateException(String.format("HTTP status: %d reason: %s", response.statusCode(), errorMessage));
        }

        response.then()
                .assertThat()
                .body("ok", equalTo(true));

        Long id = authenticate(courier);

        return courier.setId(id);
    }

    public Long authenticate(Courier courier) {
        Response response = CourierRestApiClient.authenticate(courier);

        if(response.statusCode() != HttpStatus.SC_OK) {
            String errorMessage = response.jsonPath().getString("message");
            throw new CourierAuthenticationException(String.format("HTTP status: %d reason: %s", response.statusCode(), errorMessage));
        }

        return response.then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", notNullValue())
                .extract().response().jsonPath().getLong("id");
    }

    public void delete(Courier courier) {
        if(courier.getId() == null) {
            throw new IllegalStateException("Courier does not have ID required for delete");
        }
        delete(courier.getId());
    }

    public void delete(Long courierId) {
        Response response = CourierRestApiClient.delete(courierId);

        if(response.statusCode() != HttpStatus.SC_OK) {
            throw new CourierDeleteException(String.format("HTTP status: %d reason: %s", response.statusCode(), response.body().asString()));
        }
    }
}
