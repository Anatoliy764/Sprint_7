package kz.yandex.practicum.qa.scooter.domain.courier.exceptions;

public class CourierDeleteException extends RuntimeException {
    public CourierDeleteException(String message) {
        super(message);
    }
}
