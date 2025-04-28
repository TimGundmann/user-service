package dk.gundmann.users.mail;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class UrlToken {

	private String email;
	private String secret;
	private String token;
	private long milliseconds;

	public static UrlToken aBuilder() {
		return new UrlToken();
	}
	
	public UrlToken token(String token) {
		this.token = token;
		return this;
	}
	
	public UrlToken email(String email) {
		this.email = email;
		return this;
	}

	public UrlToken secret(String secret) {
		this.secret = secret;
		return this;
	}

	public UrlToken expirationTime(long milliseconds) {
		this.milliseconds = milliseconds;
		return this;
	}

	public String build() {
    	return Jwts.builder()
    			.setSubject(email)
    			.setExpiration(new Date(System.currentTimeMillis() + milliseconds))
    			.signWith(SignatureAlgorithm.HS512, secret)
        .compact();
	}
	
	public String parsEmail() {
		return Jwts.parser()
				.setSigningKey(secret)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

}
