package dk.gundmann.users.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.mail.UrlToken;
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
		verifyAndSaveNewUser(user);
		mailService.sendActivationMail(user);
	}

	public boolean activate(String token) {
		return repository.findById(UrlToken.aBuilder()
				.secret(securityConfig.getSecret())
				.token(token)
				.parsEmail())
			.map(user -> {
				user.setActive(true);
				repository.save(user);
				return true;
			}).orElse(false);
	}
	
	public void passwrodReset(String email) {
		repository.findById(email)
			.orElseThrow(() -> new UserExistsException("Brugeren findes ikke!"));
		this.mailService.sendPasswordChangeMail(email);
	}
	
	public List<String> findAllEmailsWithRole(String role) {
		return this.repository.findAllWithRole(role).stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList());
	}

	public void busSignUp(User user) throws MessagingException {
		verifyAndSaveNewUser(user);
		mailService.sendActivationMailToAdmin(user, findAllEmailsWithRole("ADMIN"));
	}
	
	public void toggleActivation(String email, boolean active) {
		repository.findById(email).ifPresent(user -> { 
			user.setActive(active);
			repository.save(user);
		});
	}

	public void update(User user) {
		repository.save(user);
	}

	public void delete(User user) {
		repository.delete(user);		
	}

	public Collection<String> findAllEmailsNotification(String type) {
		return repository.findAllEmailsNotification(type).stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList());
	}

	public boolean newPassword(String password, String token) {
		return repository.findById(UrlToken.aBuilder()
				.secret(securityConfig.getSecret())
				.token(token)
				.parsEmail())
			.map(user -> {
				user.setPassword(passwordEncoder.encode(password));
				repository.save(user);
				return true;
			}).orElse(false);
	}

	private void verifyAndSaveNewUser(User user) {
		repository.findById(user.getEmail()).ifPresent(u -> { throw new UserExistsException("Brugeren findes allerede!"); });
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setActive(false);
		user.getRoles().add("USER");
		repository.save(user);
	}

}
