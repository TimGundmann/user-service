package dk.gundmann.users.user;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import dk.gundmann.security.IsAdmin;
import dk.gundmann.users.mail.IMailService;

@RestController
@RequestMapping("/")
@ControllerAdvice
class UserController {

	private UserService service;
	private IMailService mailService;

	public UserController(UserService service, IMailService mailService) {
		this.service = service;
		this.mailService = mailService;
	}

	@ExceptionHandler(UserExistsException.class)
	public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {
		return new ResponseEntity<>(ErrorDetails.builder()
				.timestamp(LocalDateTime.now())
				.message(ex.getMessage())
				.details(request.getDescription(false))
				.build(), 
			HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleException(Exception ex, WebRequest request) {
		return new ResponseEntity<>(ErrorDetails.builder()
				.timestamp(LocalDateTime.now())
				.message(ex.getMessage())
				.details(request.getDescription(false))
				.build(), 
			HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/all")
	public List<User> users() {
		return service.users();
	}

	@GetMapping("/current")
	public User current(Principal principal) {
		return service.findActiveUser(principal.getName()).get();
	}

	@PostMapping("/signup")
	public void signUp(@RequestBody User user) throws MessagingException {
		service.signUp(user);
	}

	@PostMapping("/{email}/active/{active}")
	@IsAdmin
	public void changeActivation(@PathVariable String email, @PathVariable boolean active) throws MessagingException {
		service.toggleActivation(email, active);
	}

	@PostMapping("/bussignup")
	public void busSignUp(@RequestBody User user) throws MessagingException {
		service.busSignUp(user);
	}

	@PostMapping("/activate")
	public ResponseEntity<Void> activate(@RequestBody String token) {
		if (service.activate(token)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.GONE);
	}
	
	@PostMapping("/update")
	public void update(@RequestBody User user) {
		service.update(user);
	}
	
	@PostMapping("/{email}/updatepassword")
	public ResponseEntity<Void> updatePassword(@PathVariable String email, @RequestBody String password) {
		if (service.updatePassword(email, password)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.GONE);
	}

	@PostMapping("/delete")
	public void delete(@RequestBody User user) {
		service.delete(user);
	}
	
	@PostMapping("/{email}/password/reset")
	public void passwordReset(@PathVariable String email) {
		service.passwrodReset(email);
	}
	
	@PostMapping("/{token}/password/new")
	public ResponseEntity<Void> newPassowrd(@RequestBody String password, @PathVariable String token) {
		if (service.newPassword(password, token)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.GONE);
	}

	@PostMapping("/contactMail")
	public void sendMailToAdmin(@RequestBody String content) throws MessagingException {
		this.mailService.sendMailTo(content, service.findAllEmailsWithRole("ADMIN"));
	}

	@PostMapping("/notification/{type}")
	public void sendNotification(@PathVariable String type) throws MessagingException {
		Collection<String> mails = service.findAllEmailsNotification(type);
		if (mails != null && !mails.isEmpty()) {
			this.mailService.sendNotificationMailTo(mails, type);
		}
	}

}
