package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.User;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles posting deals. */
@WebServlet("/api/deals")
public class DealServlet extends HttpServlet {

  private final UserService userService;
  private final DealManager dealManager;
  private final UserManager userManager;
  private final RestaurantManager restaurantManager;

  public DealServlet(
      DealManager dealManager,
      UserManager userManager,
      UserService userService,
      RestaurantManager restaurantManager) {
    this.dealManager = dealManager;
    this.userManager = userManager;
    this.userService = userService;
    this.restaurantManager = restaurantManager;
  }

  public DealServlet() {
    userService = UserServiceFactory.getUserService();
    dealManager = new DealManagerDatastore();
    userManager = new UserManagerDatastore();
    restaurantManager = new RestaurantManagerDatastore();
  }

  /** Posts the deal with the given id parameter */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String description = request.getParameter("description");
    String photoBlobkey = ImageUploader.getUploadedImageBlobkey(request, "pic");
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String source = request.getParameter("source");

    String email = userService.getCurrentUser().getEmail();
    User poster = userManager.readUserByEmail(email);
    long posterId = poster.id;

    long restaurantId;
    try {
      restaurantId = Long.parseLong(request.getParameter("restaurant"));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (restaurantManager.readRestaurant(restaurantId) == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // validate required parameters exist
    if (anyEmpty(description, photoBlobkey, start, end)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // validate dates
    if (!isValidDate(start) || !isValidDate(end)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (start.compareTo(end) > 0) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String tagParameter = request.getParameter("tags");
    List<String> tagNames = new ArrayList<>();
    if (tagParameter != null && !tagParameter.isEmpty()) {
      tagNames = Arrays.asList(tagParameter.split(","));
    }

    Deal deal =
        dealManager.createDeal(
            description, photoBlobkey, start, end, source, posterId, restaurantId, tagNames);

    response.sendRedirect("/deals/" + deal.id);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Deal> deals = dealManager.getAllDeals();
    response.setContentType("application/json;");
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
    response.getWriter().println(JsonFormatter.getDealListJson(deals));
  }

  private boolean anyEmpty(String... strs) {
    for (String str : strs) {
      if (str == null || str.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private boolean isValidDate(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    try {
      formatter.parse(date);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
