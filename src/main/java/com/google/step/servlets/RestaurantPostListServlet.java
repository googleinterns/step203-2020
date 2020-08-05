package com.google.step.servlets;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.datamanager.RestaurantPlaceManagerDatastore;
import com.google.step.model.Restaurant;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting restaurants. */
@WebServlet("/api/restaurants")
public class RestaurantPostListServlet extends HttpServlet {

  private final RestaurantManager restaurantManager;
  private final RestaurantPlaceManager restaurantPlaceManager;

  public RestaurantPostListServlet(
      RestaurantManager restaurantManager, RestaurantPlaceManager restaurantPlaceManager) {
    this.restaurantManager = restaurantManager;
    this.restaurantPlaceManager = restaurantPlaceManager;
  }

  public RestaurantPostListServlet() {
    restaurantManager = new RestaurantManagerDatastore();
    restaurantPlaceManager = new RestaurantPlaceManagerDatastore();
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

    Restaurant restaurant = restaurantManager.createRestaurantWithBlobKey(name, photoBlobkey);
    List<String> placeIds = Arrays.asList(places.split(","));
    restaurantPlaceManager.updatePlacesOfRestaurant(restaurant.id, placeIds);

    response.sendRedirect("/restaurant/" + restaurant.id);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Restaurant> restaurants = restaurantManager.getAllRestaurants();

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getRestaurantListBriefJson(restaurants));
  }
}
