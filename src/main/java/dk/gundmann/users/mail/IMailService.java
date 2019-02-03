package dk.gundmann.users.mail;

import dk.gundmann.users.user.User;

public interface IMailService {

    void sendActivationMail(User user);

}
