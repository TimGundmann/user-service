package dk.gundmann.users.securty;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dk.gundmann.security.JWTAuthenticationFilter;
import dk.gundmann.security.RemoteAddressResolver;
import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.UserService;

@Configuration
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private AuthenticationProvider authenticationProvider;
	private SecurityConfig securityConfig;
	private UserService userService;
	private RemoteAddressResolver addressResolver;

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
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.cors()
        .and()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			.authorizeRequests()
        		.antMatchers(HttpMethod.POST, "/login", "/signup", "/bussignup", "/activate", "/contactMail", "/**/password/**").permitAll()
        	.anyRequest().authenticated()
        .and()
        	.addFilterBefore(new JWTLoginFilter("/login", authenticationManager(), userService, securityConfig, addressResolver),
                    UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthenticationFilter(securityConfig, addressResolver),
                    UsernamePasswordAuthenticationFilter.class);

        
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
    
}