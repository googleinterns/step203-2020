package com.google.step.servlets;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.model.Restaurant;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting restaurants. */
@WebServlet("/api/restaurant/*")
public class RestaurantPostServlet extends HttpServlet {

  private RestaurantManager manager = new RestaurantManagerDatastore();

  public RestaurantPostServlet(RestaurantManager restaurantManager) {
    manager = restaurantManager;
  }

  /** Posts the restaurant with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String photoBlobkey = "A_BLOB_KEY";

    Restaurant restaurant =
        manager.createRestaurant(name, photoBlobkey);

    response.sendRedirect("/restaurant/"+restaurant.id);
  }
}
