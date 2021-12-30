package springbook.user.service.impl;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DumiMailSender implements MailSender {

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        simpleMessage.toString();
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        simpleMessages[0].toString();
    }
    
}
