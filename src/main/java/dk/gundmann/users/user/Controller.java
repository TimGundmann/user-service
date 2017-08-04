package dk.gundmann.users.user;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Controller {

	private Repository repository;

	public Controller(Repository repository) {
		this.repository = repository;
	}
	
	@RequestMapping("/users")
	public List<User> users() {
		return newArrayList(this.repository.findAll());
	}
	
}
