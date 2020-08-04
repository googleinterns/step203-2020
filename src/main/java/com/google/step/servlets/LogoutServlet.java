package com.google.step.servlets;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/logout")
public class LogoutServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      response.sendRedirect("/_gcp_iap/clear_login_cookie");
    } else {
      Cookie loginCookie = new Cookie("dev_appserver_login", "");
      loginCookie.setMaxAge(0);
      loginCookie.setPath("/");
      response.addCookie(loginCookie);
      String targetUrl = request.getHeader("referer"); // redirect to same page called;
      response.sendRedirect(targetUrl);
    }
  }
}
