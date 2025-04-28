package dk.gundmann.users.mail;

import dk.gundmann.users.user.User;

import java.util.Collection;

public interface IMailService {

    void sendActivationMail(User user);
    void sendMailTo(String content, Collection<String> mails);
    void sendActivationMailToAdmin(User user, Collection<String> adminMails);
    void sendNotificationMailTo(Collection<String> adminMails, String type);
    void sendPasswordChangeMail(String email);
    
}
