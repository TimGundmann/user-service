package dk.gundmann.users.user;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="password")
@Table(name = "`user`")
public class User {

	@Id
	private String email;
	
	@Column(unique=true)
	private String number;
	
	@Nonnull
	private String name;

	@Nonnull
	private String password;

	private boolean active;
	
	@Builder.Default
	@ElementCollection(fetch=FetchType.EAGER) 	
	private Set<String> roles = new HashSet<>();
	
	@Lob
	private String picture;
	
	@Builder.Default
	@ElementCollection(fetch=FetchType.EAGER) 	
	private Set<String> notifications = new HashSet<>();
	
}
