package dk.gundmann.users.user;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.gundmann.security.IsAdmin;
import dk.gundmann.users.mail.IMailService;

@RestController
@RequestMapping("/")
class UserController {

	private UserService service;
	private IMailService mailService;

	public UserController(UserService service, IMailService mailService) {
		this.service = service;
		this.mailService = mailService;
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

	@PostMapping("/activate")
	public void activate(@RequestBody String token) {
		service.activate(token);
	}
	
	@PostMapping("/contactMail")
	public void sendMailToAdmin(@RequestBody String content) throws MessagingException {
		this.mailService.sendMailToAdmin(content, service.findAllWithRole("ADMIN").stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList()));
	}

}
