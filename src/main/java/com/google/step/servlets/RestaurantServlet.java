package com.google.step.servlets;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Restaurant;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles individual restaurants */
@WebServlet("/api/restaurant/*")
public class RestaurantServlet extends HttpServlet {

  private RestaurantManager manager;

  public RestaurantServlet(RestaurantManager restaurantManager) {
    manager = restaurantManager;
  }

  public RestaurantServlet() {
    manager = new RestaurantManagerDatastore();
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
    manager.deleteRestaurant(id);
  }

  /** Gets the restaurant with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    } catch (StringIndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Restaurant restaurant = manager.readRestaurant(id);
    if (restaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getRestaurantJson(restaurant));
  }

  /** Updates a restaurant with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    } catch (StringIndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String name = request.getParameter("name");
    String photoBlobkey = "A_BLOB_KEY"; // TODO Blobkey
    Restaurant restaurant = new Restaurant(id, name, photoBlobkey);
    Restaurant updatedRestaurant = manager.updateRestaurant(restaurant);
    if (updatedRestaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.getWriter().println(JsonFormatter.getRestaurantJson(updatedRestaurant));
    }
  }
}