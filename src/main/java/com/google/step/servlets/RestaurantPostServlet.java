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
@WebServlet("/api/restaurants")
public class RestaurantPostServlet extends HttpServlet {

  private RestaurantManager manager;

  public RestaurantPostServlet(RestaurantManager restaurantManager) {
    manager = restaurantManager;
  }

  public RestaurantPostServlet() {
    manager = new RestaurantManagerDatastore();
  }

  /** Posts the restaurant with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");
    String photoBlobkey = ImageUploader.getUploadedImageBlobkey(request, "pic");

    Restaurant restaurant = manager.createRestaurantWithBlobKey(name, photoBlobkey);

    response.sendRedirect("/restaurant/" + restaurant.id);
  }
}
