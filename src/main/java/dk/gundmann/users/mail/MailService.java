package dk.gundmann.users.mail;

import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
 
    public void sendActivationMail(User user) throws MessagingException {
    	MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setSubject("Activation");
		helper.setFrom("noreply@gundmann.dk");
    	helper.setTo(user.getEmail());

    	helper.setText("<html><body><h2>Welcome to gundmann.dk</h2> <p>Press this link to activate you <a href=\"http://localhost:4200/#/activate/" + makeLinkToken(user.getEmail()) + "\">link</a></p></body></html>", true);

    	emailSender.send(message);    	
    }

    public void sendActivationMailToAdmin(User user, Collection<String> adminMails) throws MessagingException {
    	MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setSubject("Aktivering");
		helper.setFrom("noreply@gundmann.dk");
    	helper.setTo(adminMails.toArray(new String[adminMails.size()]));

    	helper.setText("<html><body>"
    			+ "<h2>Aktivering af ny bruger</h2>"
    			+ "<br/>"
    			+ "<p> Følgende bruger har anmodedet om oprettelse hos Bus Roskilde:</p>"
    			+ "<p>" + user.toString() + "</p>" 
    			+ "<br/>"
    			+ "<p><a href=\"http://www.gundmann.dk/bus/#/activate/" + makeLinkToken(user.getEmail()) + "\">Aktiver brugeren</a></p>"
    			+ "<br/>"
    			+ "<p>Med venlig hilsen</p>"
    			+ "<p>Bus roskilde</p>"
    			+ "</body></html>", true);

    	emailSender.send(message);    	
    }

    public void sendMailTo(String content, Collection<String> adminMails) throws MessagingException {
    	MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setSubject("Kontakt");
		helper.setFrom("noreply@gundmann.dk");
    	helper.setTo(adminMails.toArray(new String[adminMails.size()]));
    	helper.setText(content, true);
    	emailSender.send(message);    	
    }

    private String makeLinkToken(String email) {
    	return ActivationToken.aBuilder()
    			.email(email)
    			.secret(securityConfig.getSecret())
    			.build();
    }
	
}
