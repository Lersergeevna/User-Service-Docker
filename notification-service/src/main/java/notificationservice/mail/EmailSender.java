package notificationservice.mail;

/**
 * Абстракция отправки письма.
 */
public interface EmailSender {

    void send(String to, String subject, String text);
}
