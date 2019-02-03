package dk.gundmann.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.securty.AccountCredentials;
import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class LoginTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Autowired
    private TestRestTemplate template;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Test
	public void verifyThatIfNotAuthorizedThenError() throws Exception {
		// given when then
		assertEquals(HttpStatus.FORBIDDEN, template.getForEntity("/test", String.class).getStatusCode());
	}

	@Test
	public void verifyThatActuatorNotNeedAuthentication() throws Exception {
		// given when then
		assertEquals(HttpStatus.OK, template.getForEntity("/actuator/info", String.class).getStatusCode());
	}

	@Test
	public void verifyThatItIsPossibleToLogin() throws Exception {
		// given
		createAUser();

		// when then
		assertThat(login().getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void verifyThatTokenIsSet() throws Exception {
		// given
		createAUser();

		// when then
		List<String> token = login().getHeaders().get(securityConfig.getHeaderString());
		assertThat(token).isNotNull();
		assertThat(token).isNotEmpty();
		assertThat(token.get(0)).isNotEmpty();
	}
	
	@Test
	public void verifyThatTokenCanBeReused() throws Exception {
		// given
		createAUser();

		// when 
		String token = login().getHeaders().get(securityConfig.getHeaderString()).get(0);
	
		HttpHeaders headers = new HttpHeaders();
		headers.set(securityConfig.getHeaderString(), token);

		// then
		assertEquals(HttpStatus.OK, template.exchange("/current", HttpMethod.GET, new HttpEntity<>(headers), String.class).getStatusCode());
	}


	private User createAUser() {
		return userRepository.save(User.builder()
				.email("test@test.com")
				.password(passwordEncoder.encode("password"))
				.build());
	}

	private ResponseEntity<String> login() {
		return template.postForEntity("/login", AccountCredentials.builder()
				.username("test@test.com")
				.password("password")
				.build(), 
				String.class);
	}

}
