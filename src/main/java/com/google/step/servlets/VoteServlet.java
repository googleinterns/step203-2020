package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.DealVoteManager;
import com.google.step.datamanager.DealVoteManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import com.google.step.model.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles voting */
@WebServlet("/api/vote/*")
public class VoteServlet extends HttpServlet {

  private final UserService userService;
  private final UserManager userManager;
  private final VoteManager voteManager;
  private final DealVoteManager dealVoteManager;

  public VoteServlet(
      UserService userService,
      UserManager userManager,
      VoteManager manager,
      DealVoteManager dealVoteManager) {
    this.userService = userService;
    this.userManager = userManager;
    this.voteManager = manager;
    this.dealVoteManager = dealVoteManager;
  }

  public VoteServlet() {
    userService = UserServiceFactory.getUserService();
    userManager = new UserManagerDatastore();
    voteManager = new VoteManagerDatastore();
    dealVoteManager = new DealVoteManagerDatastore();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User poster = userManager.readUserByEmail(email);
    long userId = poster.id;

    if (request.getPathInfo() == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    long dealId;
    try {
      dealId = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String dir = request.getParameter("dir");
    if (dir == null || (!dir.equals("1") && !dir.equals("-1") && !dir.equals("0"))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    int prevDir = voteManager.getDirection(userId, dealId);
    int dirInt = Integer.parseInt(dir);
    voteManager.vote(userId, dealId, dirInt);
    if (prevDir == 0) { // user hasnt voted on it before
      dealVoteManager.updateDealVotes(dealId, dirInt);
    } else if (prevDir != dirInt) { // user voted
      if (dirInt == 0) {
        dealVoteManager.updateDealVotes(dealId, -prevDir);
      } else {
        dealVoteManager.updateDealVotes(dealId, dirInt * 2);
      }
    }
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User poster = userManager.readUserByEmail(email);
    long userId = poster.id;

    if (request.getPathInfo() == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    long dealId;
    try {
      dealId = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
    response.getWriter().println(voteManager.getDirection(userId, dealId));
  }
}
