package dk.gundmann.users.user;

@org.springframework.stereotype.Service
public class Service {

	private Repository repository;

	public Service(Repository repository) {
		this.repository = repository;
	}
	
	public User findByEmail(String email) {
		return repository.findOne(email);
	}
	
}
