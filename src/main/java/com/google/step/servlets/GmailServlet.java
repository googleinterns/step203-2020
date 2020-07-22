package com.google.step.servlets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/gmail")
public class GmailServlet extends HttpServlet {
  private static final String APPLICATION_NAME = "Deal Finder";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
  private UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    NetHttpTransport HTTP_TRANSPORT;
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
      return;
    }

    Gmail service =
        new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();

    try {
      sendMessage(
          service,
          "yuxinj@google.com",
          createEmail(
              "yuxinj@google.com", "yuxinj@google.com", "New Deal", "There's a new deal post."));
      // TODO get receiver and sender
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = GmailServlet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(4000).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  /**
   * Create a MimeMessage using the parameters provided.
   *
   * @param to email address of the receiver
   * @param from email address of the sender, the mailbox account
   * @param subject subject of the email
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   * @throws MessagingException
   */
  private MimeMessage createEmail(String to, String from, String subject, String bodyText)
      throws MessagingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(from));
    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }

  /**
   * Create a message from an email.
   *
   * @param emailContent Email to be set to raw of message
   * @return a message containing a base64url encoded email
   * @throws IOException
   * @throws MessagingException
   */
  private Message createMessageWithEmail(MimeMessage emailContent)
      throws MessagingException, IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

  /**
   * Send an email from the user's mailbox to its recipient.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me" can be used to indicate the
   *     authenticated user.
   * @param emailContent Email to be sent.
   * @return The sent message
   * @throws MessagingException
   * @throws IOException
   */
  private Message sendMessage(Gmail service, String userId, MimeMessage emailContent)
      throws MessagingException, IOException {
    Message message = createMessageWithEmail(emailContent);
    message = service.users().messages().send(userId, message).execute();
    return message;
  }
}
