package dk.gundmann.users.securty;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final long EXPIRATIONTIME = 864_000_000; // 10 days

	private SecurityConfig securityConfig;

	private UserService userService;

    public JWTLoginFilter(String url, AuthenticationManager authManager, UserService userService, SecurityConfig securityConfig) {
        super(new AntPathRequestMatcher(url));
		this.userService = userService;
        setAuthenticationManager(authManager);
        this.securityConfig = securityConfig;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {
        AccountCredentials creds = new ObjectMapper()
                .readValue(req.getInputStream(), AccountCredentials.class);
        return userService.findActiveByEmail(creds.getUsername()).map(user -> 
        	getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        user.getRoles().stream()
    							.map(role -> new SimpleGrantedAuthority(role))
    							.collect(Collectors.toList())
                		)))
        		.orElse(null);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {
           	String JWT = Jwts.builder()
                    .setSubject(auth.getName())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                    .signWith(SignatureAlgorithm.HS512, securityConfig.getSecret())
                    .claim("roles", auth.getAuthorities().stream()
                    		.map(role -> "ROLE_" + role.toString())
                    		.collect(Collectors.joining(",")))
                    .compact();
            res.addHeader(securityConfig.getHeaderString(), securityConfig.getTokenPrefix()+ " " + JWT);
    }
    
    
}