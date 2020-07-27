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

/** Servlet that handles posting restaurants. */
@WebServlet("/api/restaurants")
public class RestaurantPostListServlet extends HttpServlet {

  private RestaurantManager manager;

  public RestaurantPostListServlet(RestaurantManager restaurantManager) {
    manager = restaurantManager;
  }

  public RestaurantPostListServlet() {
    manager = new RestaurantManagerDatastore();
  }

  /** Posts the restaurant with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String photoBlobkey = ImageUploader.getUploadedImageBlobkey(request, "pic");
    String places = request.getParameter("places");

    if (places == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Restaurant restaurant = manager.createRestaurantWithBlobKey(name, photoBlobkey);

    response.sendRedirect("/restaurant/" + restaurant.id);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Restaurant> restaurants = manager.getAllRestaurants();

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getRestaurantListBriefJson(restaurants));
  }
}
