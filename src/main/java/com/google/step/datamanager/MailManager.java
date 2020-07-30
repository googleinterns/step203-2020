package com.google.step.datamanager;

import com.google.step.model.Deal;
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

  private static final String SENDER = "noreply@capstone-2020-dealfinder.appspotmail.com";
  private static final String HOST_URL = "https://capstone-2020-dealfinder.an.r.appspot.com/";

  /**
   * Sends emails to notify the recipients of a new deal posted by the poster.
   *
   * @param recipients recipients of the notification email.
   * @param poster poster of the new deal.
   */
  public void sendNewPostNotificationMail(List<User> recipients, Deal newDeal, User poster) {
    if (recipients.isEmpty()) {
      return;
    }

    Session session = Session.getDefaultInstance(new Properties(), null);

    Message msg = new MimeMessage(session);
    try {
      msg.setFrom(new InternetAddress(SENDER, "DealFinder Team"));
      msg.setSubject("New deal post from " + poster.username);
    } catch (MessagingException | UnsupportedEncodingException e) {
      e.printStackTrace();
      return;
    }

    for (User recipient : recipients) {
      try {
        Address address = new InternetAddress(recipient.email, recipient.username);
        msg.setRecipient(Message.RecipientType.TO, address);
        msg.setContent(composeEmail(recipient, newDeal, poster), "text/html;charset=UTF-8");
        Transport.send(msg);
      } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }

  private String composeEmail(User recipient, Deal newDeal, User poster) {
    return String.format(
        "Dear %s,<br>"
            + "There is a new deal posted by %s: <a href='%s/deals/%d'>%s</a>.<br><br>"
            + "Yours sincerely,<br>DealFinder Team",
        recipient.username, poster.username, HOST_URL, newDeal.id, newDeal.description);
  }
}
