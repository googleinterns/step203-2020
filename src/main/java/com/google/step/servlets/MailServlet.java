package com.google.step.servlets;

import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.MailManager;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/mail")
@SuppressWarnings("serial")
public class MailServlet extends HttpServlet {

  private MailManager mailManager = new MailManager();
  private UserManager userManager = new UserManagerDatastore();
  private FollowManager followManager = new FollowManagerDatastore();

  public MailServlet() {}

  public MailServlet(
      MailManager mailManager, UserManager userManager, FollowManager followManager) {
    this.mailManager = mailManager;
    this.userManager = userManager;
    this.followManager = followManager;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String posterIdString = request.getParameter("poster-id");
    long posterId;
    User poster;
    try {
      posterId = Long.parseLong(posterIdString);
      poster = userManager.readUser(posterId);
    } catch (NullPointerException | IllegalArgumentException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<Long> followerIds = new ArrayList<>(followManager.getFollowerIdsOfUser(posterId));
    List<User> followers = userManager.readUsers(followerIds);
    mailManager.sendNewPostNotificationMail(followers, poster);
  }
}
