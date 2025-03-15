package kz.yandex.practicum.qa.scooter.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ScooterRentUrlUtil {

    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1";

    public static final String ORDER_PATH = "/orders";
    public static final String ORDER_CANCEL_PATH = ORDER_PATH + "/cancel?track=";

    public static final String COURIER_PATH = "/courier";
    public static final String COURIER_LOGIN_PATH = COURIER_PATH + "/login";

}
