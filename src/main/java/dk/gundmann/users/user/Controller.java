package dk.gundmann.users.user;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
class Controller {

	private Service service;

	public Controller(Service service) {
		this.service = service;
	}
	
	@GetMapping
	public List<User> users() {
		return service.users();
	}

	@GetMapping("/current")
	public User current(Principal principal) {
		return service.findByEmail(principal.getName()).get();
	}
	
}
