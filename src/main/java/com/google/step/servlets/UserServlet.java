package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
  private TagManager tagManager = new TagManagerDatastore();
  private FollowManager followManager = new FollowManagerDatastore();

  public UserServlet(
      UserManager userManager,
      UserService userService,
      TagManager tagManager,
      FollowManager followManager) {
    super();
    this.userManager = userManager;
    this.userService = userService;
    this.tagManager = tagManager;
    this.followManager = followManager;
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
    List<User> following = new ArrayList<>(); // followManager.getUsersFollowedByUser(id);
    List<User> followers = new ArrayList<>(); // followManager.getUsersFollowingUser(id);
    List<Long> tagIds = followManager.getFollowedTagIds(id);
    List<Tag> tags = new ArrayList<>();
    for (Long tagId : tagIds) {
      tags.add(tagManager.readTag(tagId));
    }
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
    String tags = (String) request.getParameter("tags");
    if (tags != null) {
      updateTagsFollowedBy(id, tags);
    }

    response.sendRedirect("/user/" + user.id);
  }

  private void updateTagsFollowedBy(long userId, String tagsString) {
    String[] tagNames = getTagNames(tagsString);
    List<Long> tagIds = new ArrayList<>();
    for (String tagName : tagNames) {
      tagIds.add(tagManager.readOrCreateTagByName(tagName).id);
    }

    followManager.updateFollowedTagIds(userId, tagIds);
  }

  /**
   * Returns an array of tag names parsed from the tagsArrayString.
   *
   * @param tagsArrayString a string representation of a tag names array.
   * @return an array of tag names.
   */
  private String[] getTagNames(String tagsArrayString) {
    String[] tagNames = tagsArrayString.split(",");
    return tagNames;
  }
}
