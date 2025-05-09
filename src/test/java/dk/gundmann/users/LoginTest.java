package dk.gundmann.users;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.securty.AccountCredentials;
import dk.gundmann.users.user.User;
import dk.gundmann.users.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
	public void verifyThatIfNotAdminThenNotAuthorized() throws Exception {
		// given 
		createAUser();
		
		// when then
		assertEquals(HttpStatus.FORBIDDEN, template.getForEntity("/", String.class).getStatusCode());
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
		HttpHeaders headers = logInAndGetHeadersWithToken();

		// then
		assertEquals(HttpStatus.OK, template.exchange("/current", HttpMethod.GET, new HttpEntity<>(headers), String.class).getStatusCode());
	}

	private HttpHeaders logInAndGetHeadersWithToken() {
		String token = login().getHeaders().get(securityConfig.getHeaderString()).get(0);
	
		HttpHeaders headers = new HttpHeaders();
		headers.set(securityConfig.getHeaderString(), token);
		return headers;
	}


	private User createAUser() {
		return userRepository.save(User.builder()
				.email("test@test.com")
				.password(passwordEncoder.encode("password"))
				.name("Test")
				.active(true)
				.roles(Collections.singleton("USER"))
				.build());
	}

	private ResponseEntity<String> login() {
		return template.postForEntity("/login", AccountCredentials.builder()
				.username("test@test.com")
				.password(atob("password"))
				.build(), 
				String.class);
	}

	private String atob(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

}
