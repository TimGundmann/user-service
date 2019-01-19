package dk.gundmann.users.securty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountCredentials {

    private String username;
    private String password;

}
