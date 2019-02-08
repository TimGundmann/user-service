package dk.gundmann.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import dk.gundmann.users.mail.ActivationToken;
import dk.gundmann.users.mail.IMailService;
import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application-test.properties")
public class SignUpTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private TestRestTemplate template;
	
	@MockBean
	private IMailService mailService;

	@Before
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
		
		String activationToken = ActivationToken.aBuilder()
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
