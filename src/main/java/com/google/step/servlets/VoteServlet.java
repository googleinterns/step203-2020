package com.google.step.servlets;

import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles voting */
@WebServlet("/api/vote/*")
public class VoteServlet extends HttpServlet {

  private final VoteManager manager;

  public VoteServlet() {
    manager = new VoteManagerDatastore();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long user = 2345; // TODO get authenticated user id
    long deal;
    try {
      deal = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String dir = request.getParameter("dir");
    if (!dir.equals("1") && !dir.equals("-1") && !dir.equals("0")) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    manager.vote(user, deal, Integer.parseInt(dir));
  }
}
