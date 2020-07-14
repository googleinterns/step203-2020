package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.model.Deal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles retrieving deals for home page */
@WebServlet("/api/home")
public class HomePageServlet extends HttpServlet {

  private final DealManager dealManager;

  public HomePageServlet(DealManager dealManager) {
    this.dealManager = dealManager;
  }

  public HomePageServlet() {
    dealManager = new DealManagerDatastore();
  }
  /** Gets the deals for the home page */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long userId = 1; // TODO get authenticated user id
    List<Deal> trendingDeals = dealManager.getTrendingDeals();
    List<Deal> dealsByUsersFollowed = dealManager.getDealsPublishedByFollowedUsers(userId);
    List<Deal> dealsByRestaurantsFollowed =
        dealManager.getDealsPublishedByFollowedRestaurants(userId);
    List<Deal> dealsByTagsFollowed = dealManager.getDealsPublishedByFollowedTags(userId);
    List<List<Deal>> homePageDeals =
        new ArrayList<List<Deal>>(
            Arrays.asList(
                trendingDeals,
                dealsByUsersFollowed,
                dealsByRestaurantsFollowed,
                dealsByTagsFollowed));
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getHomePageJson(homePageDeals));
  }
}
