package kz.yandex.practicum.qa.scooter.courier;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Credentials implements Cloneable {
    String login;
    String password;

    @Override
    public Credentials clone() {
        return new Credentials()
                .setLogin(login)
                .setPassword(password);
    }
}
