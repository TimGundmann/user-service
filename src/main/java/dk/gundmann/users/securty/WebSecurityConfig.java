package dk.gundmann.users.securty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dk.gundmann.security.JWTAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private AuthenticationProvider authenticationProvider;

	@Autowired
	public WebSecurityConfig(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/actuator/**");
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
        		.antMatchers(HttpMethod.POST, "/login").permitAll()
        	.anyRequest().authenticated()
        .and()
        	.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthenticationFilter(),
                    UsernamePasswordAuthenticationFilter.class);

        
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
    
}