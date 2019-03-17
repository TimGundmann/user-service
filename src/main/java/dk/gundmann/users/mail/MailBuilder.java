package dk.gundmann.users.mail;

import java.util.Collection;
import java.util.HashSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailBuilder {

	private String subject;
	private Collection<String> mailsTo = new HashSet<>();
	private String text;
	private JavaMailSender emailSender;

	protected MailBuilder(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	public static MailBuilder aBuilder(JavaMailSender emailSender) {
		return new MailBuilder(emailSender);
	}

	public MailBuilder subject(String subject) {
		this.subject = subject;
		return this;
	}

	public MailBuilder to(Collection<String> mailsTo) {
		this.mailsTo.addAll(mailsTo);
		return this;
	}

	public MailBuilder to(String mailsTo) {
		this.mailsTo.add(mailsTo);
		return this;
	}

	public MailBuilder text(String text) {
		this.text = text;
		return this;
	}

	public MimeMessage build() {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
		try {
			helper.setSubject(subject);
			helper.setFrom("noreply@gundmann.dk");
	    	helper.setTo(mailsTo.toArray(new String[mailsTo.size()]));
	    	helper.setText(text, true);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
    	return message;
	}
	
	public void send() {
		emailSender.send(build());
	}
	
}
