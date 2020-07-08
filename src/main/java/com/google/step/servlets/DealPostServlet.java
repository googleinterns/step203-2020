package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.model.Deal;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting deals. */
@WebServlet("/api/deals")
public class DealPostServlet extends HttpServlet {

  private final DealManager manager;

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
    long restaurant = Long.parseLong(request.getParameter("restaurant"));
    // TODO validate all entries

    Deal deal =
        manager.createDeal(description, photoBlobkey, start, end, source, poster, restaurant);

    response.sendRedirect("/deals/" + deal.id);
  }
}
