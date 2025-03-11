package kz.yandex.practicum.qa.scooter.courier;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Courier extends Credentials implements Cloneable {

    Long id;
    String firstName;

    @Override
    public Courier clone() {
        return new Courier()
                .setId(id)
                .setLogin(login)
                .setPassword(password)
                .setFirstName(firstName);
    }

    @Override
    public Courier setPassword(String password) {
        super.setPassword(password);
        return this;
    }

    @Override
    public Courier setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    public Credentials asCredentials() {
        return new Credentials()
                .setLogin(login)
                .setPassword(password);
    }
}

