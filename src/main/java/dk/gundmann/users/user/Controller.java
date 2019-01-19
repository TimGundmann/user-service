package dk.gundmann.users.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
class Controller {

	private Repository repository;

	public Controller(Repository repository) {
		this.repository = repository;
	}
	
	@GetMapping
	public List<User> users() {
		List<User> result = List.of();
		this.repository.findAll().forEach(result::add);
		return result;
	}

	@GetMapping("/current")
	public User current() {
		return null;
	}
	
}
