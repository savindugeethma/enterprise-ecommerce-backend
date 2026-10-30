package lk.dropti.ninewood.mail;

import io.rocketbase.mail.EmailTemplateBuilder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lk.dropti.ninewood.provider.MailServiceProvider;

public abstract class Mailable implements Runnable {
    private final MailServiceProvider mailServiceProvider;
    private final EmailTemplateBuilder.EmailTemplateConfigBuilder emailTemplateConfigBuilder;

    public Mailable() {
        this.mailServiceProvider = MailServiceProvider.getInstance();
        this.emailTemplateConfigBuilder = EmailTemplateBuilder.builder();
    }

    @Override
    public void run() {
        try {
            System.out.println("\u001B[34m--- Mail Thread Started (Brevo Platform) --- \u001B[0m");
            System.out.println("Reading Host: " + mailServiceProvider.getProperties().getProperty("mail.smtp.host"));
            System.out.println("Reading Port: " + mailServiceProvider.getProperties().getProperty("mail.smtp.port"));

            System.out.println("Sending From: geethma423@gmail.com");

            Session mailSession = Session.getInstance(mailServiceProvider.getProperties(), mailServiceProvider.getAuthenticator());
            MimeMessage mimeMessage = new MimeMessage(mailSession);

            mimeMessage.setFrom(new InternetAddress("geethma423@gmail.com", "Ninewoods Luxury"));

            build(mimeMessage);

            if (mimeMessage.getRecipients(Message.RecipientType.TO) != null && mimeMessage.getRecipients(Message.RecipientType.TO).length > 0) {
                System.out.println("\u001B[ Attempting to connect to Brevo SMTP Server and send mail...\u001B[0m");
                Transport.send(mimeMessage);
                System.out.println("\u001B[ Email sending successful via Brevo! ...\u001B[32m");
            } else {
                throw new RuntimeException("Email recipients can not be empty...");
            }
        } catch (Exception e) {
            System.out.println("\u001B[31m [MAIL THREAD ERROR]: Something went wrong while sending the email via Brevo!\u001B[0m");
            e.printStackTrace();
        }
    }

    public abstract void build(Message message) throws MessagingException;

    public EmailTemplateBuilder.EmailTemplateConfigBuilder getEmailTemplateBuilder(){
        return emailTemplateConfigBuilder;
    }
}