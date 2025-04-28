package dk.gundmann.users.securty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCredentials {

    private String username;
    private String password;
	@Builder.Default
	private Set<String> roles = new HashSet<>();

}
