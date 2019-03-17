package dk.gundmann.users.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="password")
public class User {

	@Id
	private String email;
	
	private String number;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String password;

	private boolean active;
	
	@Size(min = 1)
	@Builder.Default
	@ElementCollection(fetch=FetchType.EAGER) 	
	private Set<String> roles = new HashSet<>();
	
	@Lob
	private String picture;
	
	@Builder.Default
	@ElementCollection(fetch=FetchType.EAGER) 	
	private Set<String> notifications = new HashSet<>();
	
}
