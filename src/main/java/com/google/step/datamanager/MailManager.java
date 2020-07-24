package com.google.step.datamanager;

import com.google.step.model.User;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** A class that handles sending emails. */
public class MailManager {

  private static final String sender = "noreply@capstone-2020-dealfinder.appspotmail.com";

  /**
   * Sends emails to notify the recipients of a new deal posted by the poster.
   *
   * @param recipients recipients of the notification email.
   * @param user poster of the new deal.
   */
  public void sendNewPostNotificationMail(List<User> recipients, User user) {
    if (recipients.isEmpty()) {
      return;
    }

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(sender));

      Address[] addresses = new Address[recipients.size()];
      for (int i = 0; i < addresses.length; i++) {
        addresses[i] = new InternetAddress(recipients.get(i).email, recipients.get(i).username);
      }
      msg.addRecipients(Message.RecipientType.TO, addresses);
      msg.setSubject("New deal post from " + user.username);
      msg.setText("There is a new deal post.");
      Transport.send(msg);
    } catch (MessagingException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
