package notificationservice.mail;

import lombok.RequiredArgsConstructor;
import notificationservice.constants.Messages;
import notificationservice.exception.EmailSendingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Отправляет простые текстовые письма через SMTP.
 */
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendingException(Messages.EMAIL_SEND_FAILED, e);
        }
    }
}
