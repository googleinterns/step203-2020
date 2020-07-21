package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual restaurants */
@WebServlet("/api/restaurants/*")
public class RestaurantServlet extends HttpServlet {

  private RestaurantManager restaurantManager;
  private DealManager dealManager;

  public RestaurantServlet(RestaurantManager restaurantManager, DealManager dealManager) {
    this.restaurantManager = restaurantManager;
    this.dealManager = dealManager;
  }

  public RestaurantServlet() {
    restaurantManager = new RestaurantManagerDatastore();
  }

  /** Deletes the restaurant with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    restaurantManager.deleteRestaurant(id);
  }

  /** Gets the restaurant with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Restaurant restaurant = restaurantManager.readRestaurant(id);
    if (restaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    List<Deal> deals = dealManager.getDealsOfRestaurant(id);

    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getRestaurantJson(restaurant, deals));
  }

  /** Updates a restaurant with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String name = request.getParameter("name");
    String photoBlobkey = "A_BLOB_KEY"; // TODO Blobkey
    Restaurant restaurant = new Restaurant(id, name, photoBlobkey);
    Restaurant updatedRestaurant = restaurantManager.updateRestaurant(restaurant);
    if (updatedRestaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      List<Deal> deals = dealManager.getDealsOfRestaurant(id);
      response.getWriter().println(JsonFormatter.getRestaurantJson(updatedRestaurant, deals));
    }
  }
}
