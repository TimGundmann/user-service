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
    	MailBuilder.aBuilder(emailSender)
    		.subject("Aktivering")
    		.to(adminMails)
    		.text("<html><body>"
        			+ "<h3>Aktivering af ny bruger</h3>"
        			+ "<p>Følgende bruger har anmodedet om oprettelse hos Bus Roskilde:</p>"
        			+ "<p>" + user.getName() + "</p>" 
        			+ "<p><a href=\"http://www.gundmann.dk/bus/#/activate/" + makeLinkToken(user.getEmail()) + "\">Aktiver brugeren</a></p>"
        			+ "<p>Med venlig hilsen</p>"
        			+ "<p>Bus roskilde</p>"
        			+ "</body></html>")
    		.send();
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

	@Override
	public void sendNotificationMailTo(Collection<String> mailsTo, String type) throws MessagingException {
    	MailBuilder.aBuilder(emailSender)
		.subject("Notification")
		.to(mailsTo)
		.text("News".equals(type) ? makeMessageForNews(type) : makeMessageForPlanChange(type))
		.send();
	}

	private String makeMessageForPlanChange(String type) {
		return "<html><body>"
    			+ "<h3>Notifikation</h3>"
    			+ "<p>Der sket ændringer for " + type + " hos Bus Roskilde</p>"
    			+ "<p><a href=\"http://www.gundmann.dk/bus/#/home/plans/" + type + "\">Se ændringen her</a></p>"
    			+ "<p>Med venlig hilsen</p>"
    			+ "<p>Bus roskilde</p>"
    			+ "</body></html>";
	}
	
	private String makeMessageForNews(String type) {
		return "<html><body>"
    			+ "<h3>Nyhed</h3>"
    			+ "<p>Der er kommet en nyhed på Bus Roskilde</p>"
    			+ "<p><a href=\"http://www.gundmann.dk/bus/#/home/news\">Se nyheden her</a></p>"
    			+ "<p>Med venlig hilsen</p>"
    			+ "<p>Bus roskilde</p>"
    			+ "</body></html>";
	}
}
