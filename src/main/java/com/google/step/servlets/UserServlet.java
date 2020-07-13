package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.TagManagerDatastore;
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
  private DealManager dealManager = new DealManagerDatastore();
  private FollowManager followManager = new FollowManagerDatastore();
  private TagManager tagManager = new TagManagerDatastore();
  // TODO: private RestaurantManager restaurantManager = new RestaurantManagerDatastore();

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
    long id;
    try {
      String idString = request.getPathInfo().substring(1); // Remove '/'
      id = Long.parseLong(idString);
    } catch (IndexOutOfBoundsException | NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    User user;
    try {
      user = userManager.readUser(id);
    } catch (IllegalArgumentException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<Deal> deals = new ArrayList<>(); // dealManager.getDealsPublishedByUser(id);

    List<Long> followingIds = followManager.getFollowedUserIds(id);
    List<User> following = userManager.readUsers(followingIds);

    List<Long> followerIds = followManager.getFollowerIdsOfUser(id);
    List<User> followers = userManager.readUsers(followerIds);

    List<Long> tagIds = followManager.getFollowedTagIds(id);
    List<Tag> tags = tagManager.readTags(tagIds);

    // TODO: List<Long> restaurantIds = followManager.getFollowedRestaurantIds(id);
    List<Restaurant> restaurants =
        new ArrayList<>(); // followManager.getRestaurantsFollowedByUser(id);

    String json = JsonFormatter.getUserJson(user, deals, following, followers, tags, restaurants);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    long id;
    User user;
    try {
      String idString = request.getPathInfo().substring(1);
      id = Long.parseLong(idString);
      user = userManager.readUser(id);
    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    if (!userEmail.equals(user.email)) {
      // Inconsistent request with login status
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String username = (String) request.getParameter("username");
    String bio = (String) request.getParameter("bio");
    User updatedUser = new User(user.id, null, username, null, bio);

    userManager.updateUser(updatedUser);
    response.sendRedirect("/user/" + user.id);
  }
}
