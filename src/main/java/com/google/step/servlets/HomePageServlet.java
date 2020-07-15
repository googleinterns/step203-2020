package com.google.step.servlets;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealTagManagerDatastore;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.TagManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.datamanager.VoteManager;
import com.google.step.datamanager.VoteManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles retrieving deals for home page */
@WebServlet("/api/home")
public class HomePageServlet extends HttpServlet {

  private final DealManager dealManager;
  private final UserManager userManager;
  private final VoteManager voteManager;
  private final RestaurantManager restaurantManager;
  private final DealTagManager dealTagManager;
  private final TagManager tagManager;
  private final FollowManager followManager;

  public HomePageServlet(
      DealManager dealManager,
      UserManager userManager,
      RestaurantManager restaurantManager,
      VoteManager voteManager,
      DealTagManager dealTagManager,
      TagManager tagManager,
      FollowManager followManager) {
    this.dealManager = dealManager;
    this.userManager = userManager;
    this.voteManager = voteManager;
    this.restaurantManager = restaurantManager;
    this.dealTagManager = dealTagManager;
    this.tagManager = tagManager;
    this.followManager = followManager;
  }

  public HomePageServlet() {
    dealManager = new DealManagerDatastore();
    userManager = new UserManagerDatastore();
    voteManager = new VoteManagerDatastore();
    restaurantManager = new RestaurantManagerDatastore();
    tagManager = new TagManagerDatastore();
    dealTagManager = new DealTagManagerDatastore();
    followManager = new FollowManagerDatastore();
  }

  /** Gets the deals for the home page */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long userId = 1; // TODO get authenticated user id
    List<Deal> trendingDeals = dealManager.getTrendingDeals();
    List<Deal> dealsByUsersFollowed =
        dealManager.getDealsPublishedByFollowedUsers(followManager.getFollowedUserIds(userId));
    List<Deal> dealsByRestaurantsFollowed =
        dealManager.getDealsPublishedByFollowedRestaurants(
            followManager.getFollowedRestaurantIds(userId));
    List<Deal> dealsByTagsFollowed =
        dealManager.getDealsPublishedByFollowedTags(followManager.getFollowedTagIds(userId));
    List<List<Deal>> homePageDeals =
        new ArrayList<List<Deal>>(
            Arrays.asList(
                trendingDeals,
                dealsByUsersFollowed,
                dealsByRestaurantsFollowed,
                dealsByTagsFollowed));
    response.setContentType("application/json;");
    response.getWriter().println(JsonFormatter.getHomePageJson(getHomePageDealMaps(homePageDeals)));
  }

  /** Creates a list of list of deal maps for the home page */
  private List<List<Map<String, Object>>> getHomePageDealMaps(List<List<Deal>> homePageDeals) {
    List<List<Map<String, Object>>> homePageDealsMapList =
        new ArrayList<List<Map<String, Object>>>();
    for (List<Deal> dealList : homePageDeals) {
      List<Map<String, Object>> homePageSectionDealMaps = new ArrayList<Map<String, Object>>();
      for (Deal deal : dealList) {
        User user = userManager.readUser(deal.posterId);
        Restaurant restaurant = restaurantManager.readRestaurant(deal.restaurantId);
        List<Tag> tags = tagManager.readTags(dealTagManager.getTagIdsOfDeal(deal.id));
        int votes = voteManager.getVotes(deal.id);
        Map<String, Object> homePageDealMap =
            JsonFormatter.getBriefHomePageDealMap(deal, user, restaurant, tags, votes);
        homePageSectionDealMaps.add(homePageDealMap);
      }
      homePageDealsMapList.add(homePageSectionDealMaps);
    }
    return homePageDealsMapList;
  }
}
