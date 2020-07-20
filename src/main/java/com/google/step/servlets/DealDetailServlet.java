package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual deals. */
@WebServlet("/api/deals/*")
public class DealDetailServlet extends HttpServlet {

  private final DealManager dealManager;
  private final UserManager userManager;
  private final VoteManager voteManager;

  public DealDetailServlet() {
    dealManager = new DealManagerDatastore();
    userManager = new UserManagerDatastore();
    voteManager = new VoteManagerDatastore();
  }

  /** Deletes the deal with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // TODO: check user authentication
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    dealManager.deleteDeal(id);
  }

  /** Gets the deal with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Deal deal = dealManager.readDeal(id);
    if (deal == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // TODO get real restaurant
    Restaurant restaurant = new Restaurant(deal.restaurantId, "Restaurant Name", "ablobkey");

    User poster = userManager.readUser(deal.posterId);

    // TODO get real tags
    List<Tag> tags = new ArrayList<>();

    int votes = voteManager.getVotes(deal.id);

    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getDealJson(deal, restaurant, poster, tags, votes));
  }

  /** Updates the deal with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String description = request.getParameter("description");
    String photoBlobkey = null; // TODO connect to blobstore
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String source = request.getParameter("source");
    long posterId = -1;
    long restaurantId = -1;
    if (request.getParameter("restaurant") != null) {
      try {
        restaurantId = Long.parseLong(request.getParameter("restaurant"));
      } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      // TODO validate that restaurant ID exists
    }

    // validate dates
    if (!isValidDate(start) || !isValidDate(end)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (start.compareTo(end) > 0) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    List<String> tagNames = null; // TODO get from request parameter

    Deal deal =
        new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId, null);
    dealManager.updateDeal(deal, tagNames);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private boolean isValidDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    try {
      formatter.parse(date);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
