package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.model.Deal;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting deals. */
@WebServlet("/api/deals")
public class DealPostServlet extends HttpServlet {

  private final DealManager manager;

  public DealPostServlet(DealManager manager) {
    this.manager = manager;
  }

  public DealPostServlet() {
    manager = new DealManagerDatastore();
  }

  /** Posts the deal with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("post deal");
    String description = request.getParameter("description");
    String photoBlobkey = "TODO"; // TODO connect to blobstore
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String source = request.getParameter("source");
    long poster = 1234; // TODO get authenticated user
    long restaurant;
    try {
      restaurant = Long.parseLong(request.getParameter("restaurant"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    // TODO validate that restaurant ID exists

    // validate required parameters exist
    if (anyEmpty(description, photoBlobkey, start, end)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
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

    Deal deal =
        manager.createDeal(description, photoBlobkey, start, end, source, poster, restaurant);

    // TODO redirect to deal page instead of printing deal
    response.getWriter().println(JsonFormatter.getDealJson(deal));
  }

  private boolean anyEmpty(String... strs) {
    for (String str : strs) {
      if (str == null || str.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private boolean isValidDate(String date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    try {
      format.parse(date);
    } catch (ParseException e) {
      return false;
    }
    return true;
  }
}
