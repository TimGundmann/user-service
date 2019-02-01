package dk.gundmann.users.mail;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ActivationToken {

	private static final long EXPIRATIONTIME = 172_800_000; // two days
	private String email;
	private String secret;
	private String token;

	public static ActivationToken aBuilder() {
		return new ActivationToken();
	}
	
	public ActivationToken token(String token) {
		this.token = token;
		return this;
	}
	
	public ActivationToken email(String email) {
		this.email = email;
		return this;
	}

	public ActivationToken secret(String secret) {
		this.secret = secret;
		return this;
	}

	public String build() {
    	return Jwts.builder()
    			.setSubject(email)
    			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
    			.signWith(SignatureAlgorithm.HS512, secret)
        .compact();
	}
	
	public String pars() {
		return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

}
