package dk.gundmann.users.securty;

import dk.gundmann.security.JWTAuthenticationFilter;
import dk.gundmann.security.RemoteAddressResolver;
import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.UserService;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AutoConfigureOrder(1)
public class WebSecurityConfig {

	private final AuthenticationProvider authenticationProvider;
	private final SecurityConfig securityConfig;
	private final UserService userService;
	private final RemoteAddressResolver addressResolver;

	public WebSecurityConfig(
			AuthenticationProvider authenticationProvider,
			UserService userService,
			SecurityConfig securityConfig,
			RemoteAddressResolver addressResolver) {
		this.authenticationProvider = authenticationProvider;
		this.userService = userService;
		this.securityConfig = securityConfig;
		this.addressResolver = addressResolver;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/login", "/signup", "/bussignup", "/activate", "/contactMail", "/**/password/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/linkedin", "/auth").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(new JWTLoginFilter("/login", authenticationManager, userService, securityConfig, addressResolver),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JWTAuthenticationFilter(securityConfig, addressResolver),
						UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
