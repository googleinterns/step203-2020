package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.datamanager.RestaurantPlaceManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.User;
import java.io.IOException;
import java.util.ArrayList;
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
  private RestaurantPlaceManager restaurantPlaceManager;
  private UserManager userManager;
  private UserService userService;
  private FollowManager followManager;

  public RestaurantServlet(
      RestaurantManager restaurantManager,
      DealManager dealManager,
      RestaurantPlaceManager restaurantPlaceManager,
      UserManager userManager,
      UserService userService,
      FollowManager followManager) {
    this.restaurantManager = restaurantManager;
    this.dealManager = dealManager;
    this.restaurantPlaceManager = restaurantPlaceManager;
    this.userManager = userManager;
    this.userService = userService;
    this.followManager = followManager;
  }

  public RestaurantServlet() {
    restaurantManager = new RestaurantManagerDatastore();
    dealManager = new DealManagerDatastore();
    restaurantPlaceManager = new RestaurantPlaceManagerDatastore();
    userManager = new UserManagerDatastore();
    userService = UserServiceFactory.getUserService();
    followManager = new FollowManagerDatastore();
  }

  /** Deletes the restaurant with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User user = userManager.readUserByEmail(email);
    long posterId = restaurantManager.readRestaurant(id).posterId;
    if (posterId != user.id) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    restaurantManager.deleteRestaurant(id);
    restaurantPlaceManager.deletePlacesOfRestaurant(id);
    followManager.deleteFollowersOfRestaurant(id);
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

    User poster = userManager.readUser(restaurant.posterId);
    List<Deal> deals = dealManager.getDealsOfRestaurant(id);
    List<String> placeIds = new ArrayList<>(restaurantPlaceManager.getPlaceIdsOfRestaurant(id));

    response.setContentType("application/json;");
    response
        .getWriter()
        .println(JsonFormatter.getRestaurantJson(restaurant, poster, deals, placeIds));
  }

  /** Updates a restaurant with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String email = userService.getCurrentUser().getEmail();
    User user = userManager.readUserByEmail(email);

    Restaurant currentRestaurant = restaurantManager.readRestaurant(id);
    if (currentRestaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    if (currentRestaurant.posterId != user.id) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String name = request.getParameter("name");
    String photoBlobkey = "A_BLOB_KEY"; // TODO Blobkey
    Restaurant restaurant = Restaurant.createRestaurantWithBlobkey(id, name, photoBlobkey, user.id);
    Restaurant updatedRestaurant = restaurantManager.updateRestaurant(restaurant);
    if (updatedRestaurant == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      List<Deal> deals = dealManager.getDealsOfRestaurant(id);
      List<String> placeIds = new ArrayList<>(restaurantPlaceManager.getPlaceIdsOfRestaurant(id));
      response
          .getWriter()
          .println(JsonFormatter.getRestaurantJson(updatedRestaurant, user, deals, placeIds));
    }

    List<Deal> deals = dealManager.getDealsOfRestaurant(id);
    List<String> placeIds = new ArrayList<>(restaurantPlaceManager.getPlaceIdsOfRestaurant(id));
    response
        .getWriter()
        .println(JsonFormatter.getRestaurantJson(updatedRestaurant, user, deals, placeIds));
  }
}
