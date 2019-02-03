package dk.gundmann.users.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.User;

@Service
class MailService implements IMailService {

    private JavaMailSender emailSender;
	private SecurityConfig securityConfig;

	public MailService(JavaMailSender emailSender, SecurityConfig securityConfig) {
		this.emailSender = emailSender;
		this.securityConfig = securityConfig;
    	
    }
 
    public void sendActivationMail(User user) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(user.getEmail()); 
        message.setSubject("Activation"); 
        message.setText("Welcome to gundmann.dk\\n Press this link to activate you https://localhost:4200/activate/" + makeLinkToken(user.getEmail()));
        emailSender.send(message);
    }
    
    private String makeLinkToken(String email) {
    	return ActivationToken.aBuilder()
    			.email(email)
    			.secret(securityConfig.getSecret())
    			.build();
    }
	
}
