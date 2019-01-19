package dk.gundmann.users.user;

import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

	private Repository repository;

	public Service(Repository repository) {
		this.repository = repository;
	}
	
	public Optional<User> findByEmail(String email) {
		return repository.findById(email);
	}
	
}
