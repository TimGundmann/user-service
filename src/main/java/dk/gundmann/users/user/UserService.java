package dk.gundmann.users.user;

import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.mail.ActivationToken;
import dk.gundmann.users.mail.IMailService;

@Service
public class UserService {

	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	private SecurityConfig securityConfig;
	private IMailService mailService;

	public UserService(UserRepository repository, PasswordEncoder passwordEncoder, SecurityConfig securityConfig, IMailService mailService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.securityConfig = securityConfig;
		this.mailService = mailService;
	}
	
	public Optional<User> findActiveByEmail(String email) {
		return repository.findActiveUser(email);
	}
	
	public void createUser(String email, String password) {
		repository.save(User.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.build());
	}
	
	public List<User> users() {
		return this.repository.findAll();
	}

	public void signUp(User user) throws MessagingException {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.save(user);
		mailService.sendActivationMail(user);
	}

	public boolean activate(String token) {
		return repository.findById(ActivationToken.aBuilder()
				.secret(securityConfig.getSecret())
				.token(token)
				.pars())
			.map(user -> {
				user.setActive(true);
				repository.save(user);
				return true;
			}).orElse(false);
	}
	
	public List<User> findAllWithRole(String role) {
		return this.repository.findAllWithRole(role);
	}
	
}
