package dk.gundmann.users.securty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private AuthenticationProvider authenticationProvider;

	@Autowired
	public WebSecurityConfig(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/users/**")
    		.csrf().disable()
        	.authorizeRequests().antMatchers(HttpMethod.POST, "/users/login").permitAll()
            .and()
        	.addFilterBefore(new JWTLoginFilter("/users/login", authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
        	.antMatcher("/actuator/**")
        	.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
    
}