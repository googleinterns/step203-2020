package com.google.step.servlets;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.model.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;  

@WebServlet("/api/comments/*")
public class CommentServletPost extends HttpServlet {

  private CommentManager manager = new CommentManagerDatastore();

  public CommentServletPost(CommentManager commentManager) {
    manager = commentManager;
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
      content = request.getParameter("content");
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Comment comment = manager.createComment(dealId, userId, content);
    if (comment == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    response.sendRedirect("/deals/"+ comment.dealId);
  }
}
