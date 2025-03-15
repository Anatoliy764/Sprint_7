package kz.yandex.practicum.qa.scooter.domain.courier.exceptions;

public class CourierAuthenticationException extends RuntimeException {

    public CourierAuthenticationException(String message) {
        super(message);
    }
}
