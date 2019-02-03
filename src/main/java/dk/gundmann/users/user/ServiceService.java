package dk.gundmann.users.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.mail.ActivationToken;
import dk.gundmann.users.mail.IMailService;

@Service
public class ServiceService {

	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	private SecurityConfig securityConfig;
	private IMailService mailService;

	public ServiceService(UserRepository repository, PasswordEncoder passwordEncoder, SecurityConfig securityConfig, IMailService mailService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.securityConfig = securityConfig;
		this.mailService = mailService;
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

	public void signUp(User user) {
		repository.save(user);
		mailService.sendActivationMail(user);
	}

	public boolean activate(String token) {
		return findByEmail(ActivationToken.aBuilder()
				.secret(securityConfig.getSecret())
				.token(token)
				.pars())
			.map(user -> {
				user.setActive(true);
				repository.save(user);
				return true;
			}).orElse(false);
	}
			
}
