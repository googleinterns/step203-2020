package com.google.step.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles user upload url. */
@WebServlet("/api/user-post-url/*")
public class UserPostUrlServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      String idString = request.getPathInfo().substring(1);
      id = Long.parseLong(idString);
    } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    response.setContentType("text/html");
    response.getWriter().println(ImageUploader.getUploadUrl("/api/users/" + id);
  }
}
