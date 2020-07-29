package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Restaurant;
import com.google.step.model.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting restaurants. */
@WebServlet("/api/restaurants")
public class RestaurantPostListServlet extends HttpServlet {

  private RestaurantManager restaurantManager;
  private UserManager userManager;
  private UserService userService;

  public RestaurantPostListServlet(
      RestaurantManager restaurantManager, UserManager userManager, UserService userService) {
    this.restaurantManager = restaurantManager;
    this.userManager = userManager;
    this.userService = userService;
  }

  public RestaurantPostListServlet() {
    restaurantManager = new RestaurantManagerDatastore();
    userManager = new UserManagerDatastore();
    userService = UserServiceFactory.getUserService();
  }

  /** Posts the restaurant with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String email = userService.getCurrentUser().getEmail();
    User poster = userManager.readUserByEmail(email);

    String name = request.getParameter("name");
    String photoBlobkey = ImageUploader.getUploadedImageBlobkey(request, "pic");
    String places = request.getParameter("places");

    if (places == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Restaurant restaurant =
        restaurantManager.createRestaurantWithBlobKey(name, photoBlobkey, poster.id);

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
