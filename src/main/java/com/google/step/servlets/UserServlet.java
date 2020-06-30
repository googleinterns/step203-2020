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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    String idString = request.getPathInfo().substring(1); // Remove '/'
    long id = Long.parseLong(idString);
    User user = userManager.readUser(id);
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
    UserService userService = UserServiceFactory.getUserService();
  }
}
