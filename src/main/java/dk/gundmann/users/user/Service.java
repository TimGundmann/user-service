package dk.gundmann.users.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Service
public class Service {

	private Repository repository;
	private PasswordEncoder passwordEncoder;

	public Service(Repository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public Optional<User> findByEmail(String email) {
		return repository.findById(email);
	}
	
	public void createUser(String email, String password) {
		repository.save(User.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.build());
	}
	
	public List<User> users() {
		List<User> result = List.of();
		this.repository.findAll().forEach(result::add);
		return result;
	}

	
}
