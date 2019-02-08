package dk.gundmann.users.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	private String email;
	
	private String number;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String password;

	private boolean active;
	
	@Builder.Default
	@ElementCollection(fetch=FetchType.EAGER) 	
	private Set<String> roles = new HashSet<>();
	
}
