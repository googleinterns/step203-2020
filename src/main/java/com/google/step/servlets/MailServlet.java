package com.google.step.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mail")
@SuppressWarnings("serial")
public class MailServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    sendSimpleMail();
  }

  private void sendSimpleMail() {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("noreply@capstone-2020-dealfinder.appspotmail.com"));
      msg.addRecipient(
          Message.RecipientType.TO, new InternetAddress("yuxinj@google.com", "Mr. User"));
      msg.setSubject("New post");
      msg.setText("There is a new post.");
      Transport.send(msg);
    } catch (MessagingException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }
}
