package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/follows/*")
public class FollowServlet extends HttpServlet {

  private final FollowManager followManager;
  private final UserService userService;
  private final UserManager userManager;

  public FollowServlet(
      FollowManager followManager, UserService userService, UserManager userManager) {
    this.followManager = followManager;
    this.userService = userService;
    this.userManager = userManager;
  }

  public FollowServlet() {
    followManager = new FollowManagerDatastore();
    userService = UserServiceFactory.getUserService();
    userManager = new UserManagerDatastore();
  }

  /** Follows a restaurant, tag, or another user */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getPathInfo().length() == 0) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String pathInfo = request.getPathInfo().substring(1);
    long id = getId(pathInfo);
    if (id == -1) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User follower = userManager.readUserByEmail(email);
    long followerId = follower.id;

    if (pathInfo.startsWith("restaurants/")) {
      followManager.followRestaurant(followerId, id);
    } else if (pathInfo.startsWith("tags/")) {
      followManager.followTag(followerId, id);
    } else if (pathInfo.startsWith("users/")) {
      followManager.followUser(followerId, id);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    response.setStatus(HttpServletResponse.SC_OK);
  }

  /** Unfollows a restaurant, tag, or another user */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (request.getPathInfo().length() == 0) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String pathInfo = request.getPathInfo().substring(1);
    long id = getId(pathInfo);
    if (id == -1) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User follower = userManager.readUserByEmail(email);
    long followerId = follower.id;

    if (pathInfo.startsWith("restaurants/")) {
      followManager.unfollowRestaurant(followerId, id);
    } else if (pathInfo.startsWith("tags/")) {
      followManager.unfollowTag(followerId, id);
    } else if (pathInfo.startsWith("users/")) {
      followManager.unfollowUser(followerId, id);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.getWriter().println(false);
  }

  /**
   * Parses the String to the right of the first forward slash '/' as a long and returns it. If not
   * possible, returns -1;
   */
  private long getId(String pathInfo) {
    String[] splitPath = pathInfo.split("/");
    if (splitPath.length != 2) {
      return -1;
    }
    try {
      return Long.parseLong(splitPath[1]);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
