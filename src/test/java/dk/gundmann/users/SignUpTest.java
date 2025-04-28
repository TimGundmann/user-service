package dk.gundmann.users;

import dk.gundmann.users.mail.IMailService;
import dk.gundmann.users.mail.UrlToken;
import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SignUpTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private TestRestTemplate template;

	@MockBean
	private IMailService mailService;

	@BeforeEach
	public void removeUser() {
		userRepository.deleteAll();
	}
	
	@Test
	public void verifyThatAUserIsSignedOn() throws Exception {
		// given when then
		assertEquals(HttpStatus.OK, sigeUp()
				.getStatusCode());
		assertThat(userRepository.findById("signup@test.com")).isNotNull();
	}

	@Test
	public void verifyThatAUserIsSignedOnAndNotActivated() throws Exception {
		// given
		sigeUp();
		
		// when then
		assertThat(userRepository.findById("signup@test.com").get().isActive()).isFalse();
	}
	
	@Test
	public void verifyThatAUserIsActivateded() throws Exception {
		// given
		sigeUp();
		
		String activationToken = UrlToken.aBuilder()
				.expirationTime(5000000)
				.email("signup@test.com")
				.secret("test")
				.build();
		
		// when
		template.postForEntity("/activate", activationToken, String.class);
		
		
		// then
		assertThat(userRepository.findById("signup@test.com").get().isActive()).isTrue();
	}

	private ResponseEntity<String> sigeUp() {
		return template.postForEntity("/signup", User.builder()
				.email("signup@test.com")
				.password("1234")
				.name("Test")
				.build(), String.class);
	}
	
}
