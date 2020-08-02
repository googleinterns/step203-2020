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
      // follow from http://ptspts.blogspot.com/2011/12/how-to-log-out-from-appengine-app-only.html
      // but change from python to java
      Cookie acsidCookie = new Cookie("ACSID", "");
      acsidCookie.setMaxAge(0);
      acsidCookie.setPath("/");
      response.addCookie(acsidCookie);
      Cookie sacsidCookie = new Cookie("SACSID", "");
      sacsidCookie.setMaxAge(0);
      sacsidCookie.setPath("/");
      response.addCookie(sacsidCookie);
    } else {
      Cookie loginCookie = new Cookie("dev_appserver_login", "");
      loginCookie.setMaxAge(0);
      loginCookie.setPath("/");
      response.addCookie(loginCookie);
    }

    String targetUrl = request.getHeader("referer"); // redirect to same page called;
    response.sendRedirect(targetUrl);
  }
}
