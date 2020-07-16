package com.google.step.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets the url for posting restaurants. */
@WebServlet("/api/upload-restaurant-url")
public class RestaurantPostUrlServlet extends HttpServlet {

  /** Gets URL for posting restaurants */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.getWriter().println(ImageUploader.getUploadUrl("/api/restaurants"));
  }
}
