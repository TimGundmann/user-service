package dk.gundmann.users.user;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class User {

	@Id
	private String email;
	
	private String name;
	private String password;
	
}
