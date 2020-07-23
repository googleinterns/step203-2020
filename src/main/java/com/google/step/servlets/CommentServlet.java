package com.google.step.servlets;

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

  private CommentManager commentManager;
  private UserManager userManager;

  public CommentServlet(CommentManager commentManager, UserManager userManager) {
    this.commentManager = commentManager;
    this.userManager = userManager;
  }

  public CommentServlet() {
    commentManager = new CommentManagerDatastore();
    userManager = new UserManagerDatastore();
  }

  /** Deletes the comment with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    commentManager.deleteComment(id);
  }

  /** Updates a comment with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    String content;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
