package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
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

/** Servlet that handles individual deals. */
@WebServlet("/api/deals/*")
public class DealDetailServlet extends HttpServlet {

  private final DealManager dealManager;
  private final UserManager userManager;
  private final VoteManager voteManager;
  private final RestaurantManager restaurantManager;
  private final CommentManager commentManager;
  private final UserService userService;

  public DealDetailServlet() {
    dealManager = new DealManagerDatastore();
    userManager = new UserManagerDatastore();
    voteManager = new VoteManagerDatastore();
    restaurantManager = new RestaurantManagerDatastore();
    commentManager = new CommentManagerDatastore();
    userService = UserServiceFactory.getUserService();
  }

  public DealDetailServlet(
      DealManager dealManager,
      UserManager userManager,
      VoteManager voteManager,
      RestaurantManager restaurantManager,
      CommentManager commentManager,
      UserService userService) {
    this.dealManager = dealManager;
    this.userManager = userManager;
    this.voteManager = voteManager;
    this.restaurantManager = restaurantManager;
    this.commentManager = commentManager;
    this.userService = userService;
  }

  /** Deletes the deal with the given id parameter */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    String email = userService.getCurrentUser().getEmail();
    User currentUser = userManager.readUserByEmail(email);

    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Deal deal = dealManager.readDeal(id);
    if (deal == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // user can only delete deals they created
    if (deal.posterId != currentUser.id) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    response.setStatus(HttpServletResponse.SC_OK);
    dealManager.deleteDeal(id);
    commentManager.deleteAllCommentsOfDeal(id);
    ImageUploader.deleteImage(deal.photoBlobkey);
  }

  /** Gets the deal with the given id parameter */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Deal deal = dealManager.readDeal(id);
    if (deal == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    Restaurant restaurant = restaurantManager.readRestaurant(deal.restaurantId);

    User poster = userManager.readUser(deal.posterId);

    List<Tag> tags = dealManager.getTags(deal.id);

    int votes = voteManager.getVotes(deal.id);

    response.setContentType("application/json;");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(JsonFormatter.getDealJson(deal, restaurant, poster, tags, votes));
  }

  /** Updates the deal with the given id parameter */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id;
    try {
      id = Long.parseLong(request.getPathInfo().substring(1));
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Deal currentDeal = dealManager.readDeal(id);
    if (currentDeal == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String description = request.getParameter("description");
    String photoBlobkey = null; // TODO connect to blobstore
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String source = request.getParameter("source");
    long posterId = -1;
    long restaurantId = -1;
    if (request.getParameter("restaurant") != null) {
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
    }

    // validate dates
    if ((start != null && !isValidDate(start)) || (end != null && !isValidDate(end))) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // make sure start is before end
    String resultingStart = start;
    if (resultingStart == null) {
      resultingStart = currentDeal.start;
    }
    String resultingEnd = end;
    if (resultingEnd == null) {
      resultingEnd = currentDeal.end;
    }
    if (resultingStart.compareTo(resultingEnd) > 0) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    String tagParameter = request.getParameter("tags");
    List<String> tagNames = new ArrayList<>();
    if (tagParameter != null && !tagParameter.isEmpty()) {
      tagNames = Arrays.asList(tagParameter.split(","));
    }

    Deal deal =
        new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId, null);
    dealManager.updateDeal(deal, tagNames);
    response.setStatus(HttpServletResponse.SC_OK);
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
