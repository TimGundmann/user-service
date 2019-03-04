package dk.gundmann.users.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

	@Query("SELECT u FROM User u WHERE u.active = true and email = :email")
	Optional<User> findActiveUser(String email);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
	List<User> findAllWithRole(String role);
	
	@Query("SELECT u FROM User u LEFT OUTER JOIN u.roles r WHERE r != 'SYS'")
	List<User> findAll();

	@Query("SELECT u FROM User u JOIN u.notifications n WHERE n = :name")
	List<User> findAllEmailsNotification(String name);
}
