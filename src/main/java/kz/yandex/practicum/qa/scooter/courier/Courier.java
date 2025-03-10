package kz.yandex.practicum.qa.scooter.courier;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Courier implements Cloneable {
    Long id;
    String login;
    String password;
    String firstName;

    @Override
    public Courier clone() {
        return new Courier()
                .setId(id)
                .setLogin(login)
                .setPassword(password)
                .setFirstName(firstName);
    }
}
