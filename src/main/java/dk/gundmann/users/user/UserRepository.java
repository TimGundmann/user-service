package dk.gundmann.users.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

	@Query("SELECT u FROM User u WHERE u.active = true and email = :email")
	Optional<User> findActiveUser(String email);
	
}
