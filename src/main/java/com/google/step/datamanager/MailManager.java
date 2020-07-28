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

  private static final String sender = "noreply@capstone-2020-dealfinder.appspotmail.com";

  /**
   * Sends emails to notify the recipients of a new deal posted by the poster.
   *
   * @param recipients recipients of the notification email.
   * @param poster poster of the new deal.
   */
  public void sendNewPostNotificationMail(
      List<User> recipients, Deal newDeal, User poster, String hostUrl) {
    if (recipients.isEmpty()) {
      return;
    }

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    Message msg = new MimeMessage(session);
    try {
      msg.setFrom(new InternetAddress(sender, "DealFinder Team"));
      msg.setSubject("New deal post from " + poster.username);
    } catch (MessagingException | UnsupportedEncodingException e) {
      e.printStackTrace();
      return;
    }

    for (int i = 0; i < recipients.size(); i++) {
      try {
        Address address = new InternetAddress(recipients.get(i).email, recipients.get(i).username);
        msg.addRecipient(Message.RecipientType.TO, address);
        msg.setContent(
            composeEmail(recipients.get(i), newDeal, poster, hostUrl), "text/html;charset=UTF-8");
        Transport.send(msg);
      } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
        continue;
      }
    }
  }

  private String composeEmail(User recipient, Deal newDeal, User poster, String hostUrl) {
    return String.format(
        "Dear %s,\n"
            + "There is a new deal posted by %s: <a href='%s/deals/%d'>%s</a>.\n\n"
            + "Yours sincerely,\nDealFinder Team",
        recipient.username, poster.username, hostUrl, newDeal.id, newDeal.description);
  }
}
