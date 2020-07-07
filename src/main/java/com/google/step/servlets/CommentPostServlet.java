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
public class CommentPostServlet extends HttpServlet {

  private CommentManager manager;

  public CommentPostServlet(CommentManager commentManager) {
    manager = commentManager;
  }

  public CommentPostServlet() {
    manager = new CommentManagerDatastore();
  }
  /** Posts a comment for the deal with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long dealId;
    long userId;
    String content;
    try {
      dealId = Long.parseLong(request.getParameter("dealId"));
      userId = Long.parseLong(request.getParameter("userId"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    content = request.getParameter("content");
    Comment comment = manager.createComment(dealId, userId, content);
    response.sendRedirect("/deals/" + comment.dealId);
  }
}
