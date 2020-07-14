package com.google.step.servlets;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.model.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/comments/")
public class CommentGetPostServlet extends HttpServlet {

  private CommentManager manager;

  public CommentGetPostServlet(CommentManager commentManager) {
    manager = commentManager;
  }

  public CommentGetPostServlet() {
    manager = new CommentManagerDatastore();
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
    // List<Comment> comments = manager.getComments(dealId);
    response.setContentType("application/json;");
    // response.getWriter().println(JsonFormatter.getCommentsJson(comments));
  }

  /** Posts a comment for the deal with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long dealId;
    long userId = 3; // TODO get authenticated user id
    String content;
    try {
      dealId = Long.parseLong(request.getParameter("dealId"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    content = request.getParameter("content");
    Comment comment = manager.createComment(dealId, userId, content);
    response.sendRedirect("/deals/" + comment.dealId);
  }
}
