package dk.gundmann.users.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

	@Query("SELECT u FROM User u WHERE u.active = true and u.email = :email")
	Optional<User> findActiveUserByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.active = true and u.number = :number")
	Optional<User> findActiveUserByNumber(String number);

	@Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
	List<User> findAllWithRole(String role);

	@Override
	@Query("SELECT distinct u FROM User u left join fetch u.roles r WHERE r != 'SYS'")
	List<User> findAll();

	@Query("SELECT u FROM User u JOIN u.notifications n WHERE n = :name")
	List<User> findAllEmailsNotification(String name);
}
