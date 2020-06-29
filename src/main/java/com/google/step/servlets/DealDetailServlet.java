package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.model.Deal;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual deals. */
@WebServlet("/api/deals/*")
public class DealDetailServlet extends HttpServlet {

  private final DealManager manager;

  public DealDetailServlet() {
    manager = new DealManagerDatastore();
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
    manager.deleteDeal(id);
  }

  /** Deletes the deal with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Deal deal = manager.readDeal(id);
    if (deal == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getDealJson(deal));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: check user authentication
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    System.out.println("des: " + request.getParameter("description"));
    System.out.println("start: " + request.getParameter("start"));
    if (true) return;
    String description = request.getParameter("description");
    String photoBlobkey = null; // TODO connect to blobstore
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String source = request.getParameter("source");
    long poster = -1;
    long restaurant = -1;
    if (request.getParameter("restaurant") != null) {
      try {
        restaurant = Long.parseLong(request.getParameter("restaurant"));
      } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }

    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, poster, restaurant);
    manager.updateDeal(deal);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
