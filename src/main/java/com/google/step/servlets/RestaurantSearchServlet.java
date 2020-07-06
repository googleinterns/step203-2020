package com.google.step.servlets;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Restaurant;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles searching of restaurants. */
@WebServlet("/api/search/restaurants")
public class RestaurantSearchServlet extends HttpServlet {

  private final RestaurantManager manager;

  public RestaurantSearchServlet() {
    manager = new RestaurantManagerDatastore();
  }

  /**
   * Searches for restaurants with matching prefix.
   *
   * <p>Request format: ?query=my+prefix, where query is the query string
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String query = request.getParameter("query");
    if (query == null) {
      query = "";
    }

    List<Restaurant> restaurants = manager.searchRestaurant(query);
    // TODO format restaurants json
  }
}
