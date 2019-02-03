package dk.gundmann.users.user;

import java.security.Principal;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
class UserController {

	private ServiceService service;

	public UserController(ServiceService service) {
		this.service = service;
	}

	@GetMapping
	public List<User> users() {
		return service.users();
	}

	@GetMapping("/current")
	public User current(Principal principal) {
		return service.findActiveByEmail(principal.getName()).get();
	}

	@PostMapping("/signon")
	public void signUp(@RequestBody User user) throws MessagingException {
		service.signUp(user);
	}

	@PostMapping("/activate")
	public void activate(@RequestBody String token) {
		service.activate(token);
	}

}
