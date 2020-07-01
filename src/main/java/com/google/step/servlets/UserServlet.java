package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles users. */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

  private UserManager userManager = new UserManagerDatastore();
  private UserService userService = UserServiceFactory.getUserService();

  public UserServlet(UserManager userManager, UserService userService) {
    super();
    this.userManager = userManager;
    this.userService = userService;
  }

  public UserServlet() {
    super();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    long id;
    try {
      String idString = request.getPathInfo().substring(1); // Remove '/'
      id = Long.parseLong(idString);
    } catch (IndexOutOfBoundsException | NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    User user = userManager.readUser(id);
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<Deal> deals = new ArrayList<>(); // dealManager.getDealsPublishedByUser(id);
    List<User> following = new ArrayList<>(); // followManager.getUsersFollowedByUser(id);
    List<User> followers = new ArrayList<>(); // followManager.getUsersFollowingUser(id);
    List<Tag> tags = new ArrayList<>(); // followManager.getTagsFollowedByUser(id);
    List<Restaurant> restaurants =
        new ArrayList<>(); // followManager.getRestaurantsFollowedByUser(id);

    String json = JsonFormatter.getUserJson(user, deals, following, followers, tags, restaurants);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String email = userService.getCurrentUser().getEmail();

    String username = (String) request.getParameter("username");
    String bio = (String) request.getParameter("bio");
    if (username == null || bio == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    User user = userManager.readUser(email);

    userManager.updateUser(user.id, email, username, user.photoBlobKey, bio);
    response.sendRedirect("/user/" + user.id);
  }
}
