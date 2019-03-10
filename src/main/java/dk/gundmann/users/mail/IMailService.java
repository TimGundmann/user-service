package dk.gundmann.users.mail;

import java.util.Collection;

import javax.mail.MessagingException;

import dk.gundmann.users.user.User;

public interface IMailService {

    void sendActivationMail(User user);
    void sendMailTo(String content, Collection<String> mails);
    void sendActivationMailToAdmin(User user, Collection<String> adminMails);
    void sendNotificationMailTo(Collection<String> adminMails, String type);
    void sendPasswordChangeMail(String email);
    
}
