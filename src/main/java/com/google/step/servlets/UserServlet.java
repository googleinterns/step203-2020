package com.google.step.servlets;

import static com.google.step.servlets.ImageUploader.getUploadedImageBlobkey;

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
  private TagManager tagManager = new TagManagerDatastore();
  private FollowManager followManager = new FollowManagerDatastore();
  private DealManager dealManager = new DealManagerDatastore();
  // TODO: private RestaurantManager restaurantManager = new RestaurantManagerDatastore();

  public UserServlet(
      UserManager userManager,
      UserService userService,
      TagManager tagManager,
      FollowManager followManager) {
    super();
    this.userManager = userManager;
    this.userService = userService;
    this.followManager = followManager;
    this.tagManager = tagManager;
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
    } catch (IndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
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

    List<Long> followingIds = new ArrayList<>(followManager.getFollowedUserIds(id));
    List<User> following = userManager.readUsers(followingIds);

    List<Long> followerIds = new ArrayList<>(followManager.getFollowerIdsOfUser(id));
    List<User> followers = userManager.readUsers(followerIds);

    List<Long> tagIds = new ArrayList<>(followManager.getFollowedTagIds(id));
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
      String idString = request.getPathInfo().substring(1); // remove '/'
      id = Long.parseLong(idString);
      user = userManager.readUser(id);
    } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
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
    String photoBlobKey = getUploadedImageBlobkey(request, "picture");

    User updatedUser;
    if (photoBlobKey != null) {
      updatedUser = new User(user.id, null, username, photoBlobKey, bio);
    } else {
      updatedUser = new User(user.id, null, username, null, bio);
    }

    userManager.updateUser(updatedUser);
    String tags = (String) request.getParameter("tags");
    if (tags != null) {
      updateTagsFollowedBy(id, tags);
    }

    response.sendRedirect("/user/" + user.id);
  }

  private void updateTagsFollowedBy(long userId, String tagsString) {
    String[] tagNames = tagsString.split(",");
    List<Long> tagIds = new ArrayList<>();
    for (String tagName : tagNames) {
      tagIds.add(tagManager.readOrCreateTagByName(tagName).id);
    }

    followManager.updateFollowedTagIds(userId, tagIds);
  }
}
