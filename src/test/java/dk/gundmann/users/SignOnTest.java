package dk.gundmann.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import dk.gundmann.users.mail.ActivationToken;
import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application-test.properties")
public class SignOnTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private TestRestTemplate template;

	@Test
	public void verifyThatAUserIsSignedOn() throws Exception {
		// given when then
		assertEquals(HttpStatus.OK, sigeOn()
				.getStatusCode());
		assertThat(userRepository.findById("signon@test.com")).isNotNull();
	}

	@Test
	public void verifyThatAUserIsSignedOnAndNotActivated() throws Exception {
		// given
		sigeOn();
		
		// when then
		assertThat(userRepository.findById("signon@test.com").get().isActive()).isFalse();
	}
	
	@Test
	public void verifyThatAUserIsActivateded() throws Exception {
		// given
		sigeOn();
		
		String activationToken = ActivationToken.aBuilder()
				.email("signon@test.com")
				.secret("test")
				.build();
		
		// when
		template.postForEntity("/activate", activationToken, String.class);
		
		
		// then
		assertThat(userRepository.findById("signon@test.com").get().isActive()).isTrue();
	}

	private ResponseEntity<String> sigeOn() {
		return template.postForEntity("/signon", User.builder()
				.email("signon@test.com")
				.build(), String.class);
	}
}
