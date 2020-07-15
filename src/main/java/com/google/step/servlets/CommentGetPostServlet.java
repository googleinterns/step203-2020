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
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/comments")
public class CommentGetPostServlet extends HttpServlet {

  private CommentManager commentManager;
  private UserService userService;
  private UserManager userManager;

  public CommentGetPostServlet(
      CommentManager commentManager, UserService userService, UserManager userManager) {
    this.commentManager = commentManager;
    this.userService = userService;
    this.userManager = userManager;
  }

  public CommentGetPostServlet() {
    commentManager = new CommentManagerDatastore();
    userService = UserServiceFactory.getUserService();
    userManager = new UserManagerDatastore();
  }

  /** Gets the comments for the deal with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long dealId;
    try {
      dealId = Long.parseLong(request.getParameter("dealId"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    List<Comment> comments = commentManager.getCommentsForDeal(dealId);
    List<User> users =
        userManager.readUsers(
            comments.stream().map(comment -> comment.userId).collect(Collectors.toList()));
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getCommentsJson(comments, users));
  }

  /** Posts a comment for the deal with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User poster = userManager.readUserByEmail(email);
    long posterId = poster.id;

    long dealId;
    String content;
    try {
      dealId = Long.parseLong(request.getParameter("dealId"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    content = request.getParameter("content");
    if (content == null) {
      content = "";
    }
    Comment comment = commentManager.createComment(dealId, posterId, content);
    response.sendRedirect("/deals/" + dealId);
  }
}
