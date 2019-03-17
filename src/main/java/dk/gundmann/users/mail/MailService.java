package dk.gundmann.users.mail;

import java.util.Collection;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dk.gundmann.security.SecurityConfig;
import dk.gundmann.users.user.User;

@Service
class MailService implements IMailService {

	private static final long TWO_DAYES = 172_800_000;
	private static final long TWO_HOURS =   7_200_000;
	
    private JavaMailSender emailSender;
	private SecurityConfig securityConfig;

	public MailService(JavaMailSender emailSender, SecurityConfig securityConfig) {
		this.emailSender = emailSender;
		this.securityConfig = securityConfig;
    	
    }
 
    public void sendActivationMail(User user) {
    	MailBuilder.aBuilder(emailSender)
			.subject("Aktivering")
			.to(user.getEmail())
			.text("<html><body><h2>Welcome to gundmann.dk</h2> <p>Press this link to activate you <a href=\"http://localhost:4200/#/activate/" + makeLinkToken(user.getEmail(), TWO_DAYES) + "\">link</a></p></body></html>")
			.send();
    }
    
    public void sendPasswordChangeMail(String email) {
    	MailBuilder.aBuilder(emailSender)
			.subject("Nyt password")
			.to(email)
			.text("<html><body><h2>Nyt password</h2> <p>Klike på linket for at ændre dit password <a href=\"http://www.gundmann.dk/bus/#/password/" + makeLinkToken(email, TWO_HOURS) + "\">Password link</a></p></body></html>")
			.send();
    }


    public void sendActivationMailToAdmin(User user, Collection<String> adminMails) {
    	MailBuilder.aBuilder(emailSender)
    		.subject("Aktivering")
    		.to(adminMails)
    		.text("<html><body>"
        			+ "<h3>Aktivering af ny bruger</h3>"
        			+ "<p>Følgende bruger har anmodedet om oprettelse hos Bus Roskilde:</p>"
        			+ "<p>" + user.getName() + "</p>" 
        			+ "<p><a href=\"http://www.gundmann.dk/bus/#/activate/" + makeLinkToken(user.getEmail(), TWO_DAYES) + "\">Aktiver brugeren</a></p>"
        			+ "<p>Med venlig hilsen</p>"
        			+ "<p>Bus roskilde</p>"
        			+ "</body></html>")
    		.send();
    }

    public void sendMailTo(String content, Collection<String> mails) {
    	MailBuilder.aBuilder(emailSender)
			.subject("Kontakt")
			.to(mails)
			.text(content)
			.send();
    }

    private String makeLinkToken(String email, long milliseconds) {
    	return UrlToken.aBuilder()
    			.expirationTime(milliseconds)
    			.email(email)
    			.secret(securityConfig.getSecret())
    			.build();
    }

	@Override
	public void sendNotificationMailTo(Collection<String> mailsTo, String type) {
    	MailBuilder.aBuilder(emailSender)
			.subject("Notification")
			.to(mailsTo)
			.text("Nyheder".equals(type) ? makeMessageForNews(type) : makeMessageForPlanChange(type))
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
