package com.google.step.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets the url for posting deals. */
@WebServlet("/api/upload-deals-url")
public class DealPostUrlServlet extends HttpServlet {

  /** Posts the deal with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.getWriter().println(ImageProcessor.getUploadUrl("/api/deals"));
  }
}
