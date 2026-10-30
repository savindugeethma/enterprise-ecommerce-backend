package lk.dropti.ninewood.provider;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import lk.dropti.ninewood.mail.Mailable;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MailServiceProvider {
    private ThreadPoolExecutor executor;
    private Authenticator authenticator;
    private final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
    private final Properties properties = new Properties();
    private static MailServiceProvider mailServiceProvider;

    private MailServiceProvider() {
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        // 1. Brevo SMTP Settings
        properties.put("mail.smtp.host", "smtp-relay.brevo.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp-relay.brevo.com");

        // Secure Handshake
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    public static MailServiceProvider getInstance() {
        if (mailServiceProvider == null) {
            mailServiceProvider = new MailServiceProvider();

            mailServiceProvider.start();
        }
        return mailServiceProvider;
    }

    public void start() {
        // 2. Brevo Username / Password
        authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "ad14fb001@smtp-brevo.com",
                        "xsmtpsib-72dd12269575d897ce0291870694b63b957dc88a8a6ec79ae96293662f4fa0ed-y8QsvF4VMkesAP3P"
                );
            }
        };

        executor = new ThreadPoolExecutor(2, 5, 5,
                TimeUnit.SECONDS, blockingQueue, new ThreadPoolExecutor.AbortPolicy());
        executor.prestartCoreThread();
        System.out.println("\u001B[32mEmailServiceProvider Initialized with Brevo SMTP...\u001B[32m");
    }

    public Properties getProperties() {
        return properties;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void sendMail(Mailable mailable) {
        if (executor != null) {
            executor.execute(mailable);
            System.out.println("\u001B[35m[EXECUTOR]: Mail task assigned to thread pool and executed successfully!\u001B[0m");
        } else {
            boolean offer = blockingQueue.offer(mailable);
            System.out.println(" [EXECUTOR WARNING]: Executor is null, mail offered to queue instead.");
        }
    }
}