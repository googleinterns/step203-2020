package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Comment;
import com.google.step.model.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual comments. */
@WebServlet("/api/comments/*")
public class CommentServlet extends HttpServlet {

  private final CommentManager commentManager;
  private final UserManager userManager;
  private final UserService userService;

  public CommentServlet(
      CommentManager commentManager, UserManager userManager, UserService userService) {
    this.commentManager = commentManager;
    this.userManager = userManager;
    this.userService = userService;
  }

  public CommentServlet() {
    commentManager = new CommentManagerDatastore();
    userManager = new UserManagerDatastore();
    userService = UserServiceFactory.getUserService();
  }

  /** Deletes the comment with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User currentUser = userManager.readUserByEmail(email);

    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Comment comment = commentManager.readComment(id);
    if (comment == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // user can only delete comments they created
    if (comment.userId != currentUser.id) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    commentManager.deleteComment(id);
  }

  /** Updates a comment with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User currentUser = userManager.readUserByEmail(email);

    long id;
    String content;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Comment comment = commentManager.readComment(id);
    if (comment == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // user can only update comments they created
    if (comment.userId != currentUser.id) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    content = request.getParameter("content");
    Comment updatedComment = commentManager.updateComment(id, content);

    if (updatedComment == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      User poster = userManager.readUser(updatedComment.userId);
      response.getWriter().println(JsonFormatter.getCommentJson(updatedComment, poster));
    }
  }
}
