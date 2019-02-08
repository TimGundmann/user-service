package dk.gundmann.users.mail;

import java.util.Collection;

import javax.mail.MessagingException;

import dk.gundmann.users.user.User;

public interface IMailService {

    void sendActivationMail(User user) throws MessagingException;
    void sendMailTo(String content, Collection<String> mails) throws MessagingException;
    void sendActivationMailToAdmin(User user, Collection<String> adminMails) throws MessagingException;
}
