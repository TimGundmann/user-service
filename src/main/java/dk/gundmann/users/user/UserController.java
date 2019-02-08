package dk.gundmann.users.user;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping
	@IsAdmin
	public List<User> users() {
		return service.users();
	}

	@GetMapping("/current")
	public User current(Principal principal) {
		return service.findActiveByEmail(principal.getName()).get();
	}

	@PostMapping("/signup")
	public void signUp(@RequestBody User user) throws MessagingException {
		service.signUp(user);
	}

	@PostMapping("/bussignup")
	public void busSignUp(@RequestBody User user) throws MessagingException {
		service.busSignUp(user);
	}

	@PostMapping("/activate")
	public void activate(@RequestBody String token) {
		service.activate(token);
	}

	@PostMapping("/contactMail")
	public void sendMailToAdmin(@RequestBody String content) throws MessagingException {
		this.mailService.sendMailToAdmin(content, service.findAllEmailsWithRole("ADMIN"));
	}

}
