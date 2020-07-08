package com.google.step.servlets;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.model.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual comments. */
@WebServlet("/api/comments/*")
public class CommentServlet extends HttpServlet {

  private CommentManager manager;

  public CommentServlet(CommentManager commentManager) {
    manager = commentManager;
  }

  public CommentServlet() {
    manager = new CommentManagerDatastore();
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
    manager.deleteComment(id);
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
    Comment updatedComment = manager.updateComment(id, content);
    if (updatedComment == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.getWriter().println(JsonFormatter.getCommentJson(updatedComment));
    }
  }
}
