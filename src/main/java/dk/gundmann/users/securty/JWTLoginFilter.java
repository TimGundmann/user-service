package dk.gundmann.users.securty;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.gundmann.security.RemoteAddressResolver;
import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

	private final SecurityConfig securityConfig;

	private final UserService userService;

	private final RemoteAddressResolver addressResolver;

    public JWTLoginFilter(
    		String url, 
    		AuthenticationManager authManager, 
    		UserService userService, 
    		SecurityConfig securityConfig,
    		RemoteAddressResolver addressResolver) {
        super(new AntPathRequestMatcher(url));
		this.userService = userService;
		this.addressResolver = addressResolver;
        setAuthenticationManager(authManager);
        this.securityConfig = securityConfig;
    }

    @Override
    public Authentication attemptAuthentication(
			jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws AuthenticationException, IOException, jakarta.servlet.ServletException {
        AccountCredentials creds = new ObjectMapper()
                .readValue(req.getInputStream(), AccountCredentials.class);
        return userService.findActiveUser(creds.getUsername()).map(user -> 
        	getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                		user.getEmail(),
                		userService.atob(creds.getPassword()),
                        user.getRoles().stream()
    							.map(role -> new SimpleGrantedAuthority(role))
    							.collect(Collectors.toList())
                		)))
        		.orElse(null);
    }

    @Override
    protected void successfulAuthentication(
			jakarta.servlet.http.HttpServletRequest req,
			jakarta.servlet.http.HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, jakarta.servlet.ServletException {
           	String JWT = Jwts.builder()
                    .setSubject(auth.getName())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, securityConfig.getSecret())
                    .claim("ip", addressResolver.remoteAdress(req))
                    .claim("roles", auth.getAuthorities().stream()
                    		.map(role -> "ROLE_" + role.toString())
                    		.collect(Collectors.joining(",")))
                    .compact();
            res.addHeader(securityConfig.getHeaderString(), securityConfig.getTokenPrefix()+ " " + JWT);
    }
    
    
}