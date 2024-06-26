package edu.tasklynx.tasklynxjavafx.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import edu.tasklynx.tasklynxjavafx.model.Trabajador;
import edu.tasklynx.tasklynxjavafx.model.Trabajo;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class EmailSender {
    private static final String APPLICATION_NAME = "Gmail API Java Quicks";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client_secret.json";

    public EmailSender() {
    }

    private Gmail getService() throws IOException {
        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void sendTaskNotificationEmail(Trabajador trabajador, List<Trabajo> trabajos) throws IOException, MessagingException {
        Gmail service = getService();
        System.out.println("Sending email to " + trabajador.getEmail());
        MimeMessage emailContent = buildTaskNotificationEmail(trabajador, trabajos);
        sendMessage(service, "me", emailContent);
    }

    public void sendPayrollEmail(Trabajador trabajador, File payrollFile) throws IOException, MessagingException {
        Gmail service = getService();
        MimeMessage emailContent = buildPayrollEmail(trabajador, payrollFile);
        sendMessage(service, "me", emailContent);
    }

    private MimeMessage buildTaskNotificationEmail(Trabajador trabajador, List<Trabajo> trabajos) throws MessagingException {
        MimeMultipart mimeMultipart = new MimeMultipart();

        MimeBodyPart mainContent = new MimeBodyPart();
        mainContent.setContent(createMainContent(trabajador, trabajos), "text/html");

        MimeBodyPart footer = new MimeBodyPart();
        footer.setContent(footerContent, "text/html");

        mimeMultipart.addBodyPart(mainContent);
        mimeMultipart.addBodyPart(footer);

        trabajador.setEmail("almazansellesdaniel@gmail.com");

        MimeMessage emailContent = createEmail(trabajador.getEmail(), "almazansellesdaniel@gmail.com", "New tasks upcoming!", "");
        emailContent.setContent(mimeMultipart);

        return emailContent;
    }

    private MimeMessage buildPayrollEmail(Trabajador trabajador, File payrollFile) throws MessagingException, IOException {
        MimeMultipart mimeMultipart = new MimeMultipart();

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("Dear " + trabajador.getNombre() + ",\n\nPlease find attached your payroll for this month.\n\nBest regards,\nTaskLynx.");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(payrollFile);

        mimeMultipart.addBodyPart(textPart);
        mimeMultipart.addBodyPart(attachmentPart);
        
        trabajador.setEmail("almazansellesdaniel@gmail.com");

        MimeMessage emailContent = createEmail(trabajador.getEmail(), "almazansellesdaniel@gmail.com", "Your Payroll", "");
        emailContent.setContent(mimeMultipart);

        return emailContent;
    }

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, java.io.IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static void sendMessage(Gmail service, String userId, MimeMessage emailContent) throws MessagingException, java.io.IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();
        System.out.println("Message id: " + message.getId());
        System.out.println("Email sent successfully.");
    }

    private final String footerContent = """
            <footer>
                <h3>Be a Lynx, don't miss a task!</h3>
                 <p>© 2024 TaskLynx.</p>
             </footer>""";

    private String createMainContent(Trabajador trabajador, List<Trabajo> trabajos) {
        String mainContent = "<h1>Hello, " + trabajador.getNombre() + "!</h1><p>You have new upcoming tasks!</p><ul>";
        
        for (Trabajo trabajo : trabajos.stream().sorted(Comparator.comparing(Trabajo::getFecIni)).toList()) {
            mainContent += "<li>Task ID: " + trabajo.getCodTrabajo() + "</li><ul>" +
                    "<li>Description: " + trabajo.getDescripcion() + "</li>" +
                    "<li>Starting Date: " + trabajo.getFecIni() + "</li>" +
                   "<li>Priority: " + trabajo.getPrioridad() + "</li></ul>";
        }
        
        return mainContent +"</ul>";
    }
}
