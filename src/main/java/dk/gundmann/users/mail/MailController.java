package dk.gundmann.users.mail;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MailController {

	private IMailService mailService;

	MailController(IMailService mailService) {
		this.mailService = mailService;
	}
	
	@PostMapping("/contactMail")
	public void sendMailToAdmin(@RequestBody String content) throws MessagingException {
		this.mailService.sendMailToAdmin(content);
	}
	
}
