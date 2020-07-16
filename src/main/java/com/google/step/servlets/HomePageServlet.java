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

  private final Long OLDEST_DEAL_TIMESTAMP = 1594652120L; // arbitrary datetime of first deal posted
  private final String LOCATION = "Asia/Singapore";

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
    List<Deal> trendingDeals = getTrendingDeals();
    List<Deal> dealsByUsersFollowed =
        dealManager.getDealsPublishedByFollowedUsers(followManager.getFollowedUserIds(userId));
    List<Deal> dealsByRestaurantsFollowed =
        dealManager.getDealsPublishedByFollowedRestaurants(
            followManager.getFollowedRestaurantIds(userId));
    List<Deal> dealsByTagsFollowed =
        getDealsPublishedByFollowedTags(followManager.getFollowedTagIds(userId));
    List<List<Deal>> homePageDeals =
        new ArrayList<>(
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
    List<List<Map<String, Object>>> homePageDealsMapList = new ArrayList<>();
    for (List<Deal> dealList : homePageDeals) {
      List<Map<String, Object>> homePageSectionDealMaps = new ArrayList<>();
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

  /** Retrieves deals posted by tags followed by user */
  private List<Deal> getDealsPublishedByFollowedTags(List<Long> tagIds) {
    List<Long> dealIdResults = new ArrayList<>();
    for (Long id : tagIds) {
      List<Long> dealIdsWithTag = dealTagManager.getDealIdsWithTag(id);
      dealIdResults.addAll(dealIdsWithTag);
    }
    // Get rid of duplicate dealID (Deals with multiple tags)
    List<Long> dealsWithoutDuplicates = new ArrayList<>(new HashSet<>(dealIdResults));
    return readDeals(dealsWithoutDuplicates);
  }

  /** Sorts deals based on new (Newest to oldest) */
  @Override
  public List<Deal> sortDealsBasedOnNew(List<Deal> deals) {
    Collections.sort(
        deals,
        new Comparator<Deal>() {
          @Override
          public int compare(Deal deal1, Deal deal2) {
            return LocalDateTime.parse(deal2.creationTimeStamp)
                .compareTo(LocalDateTime.parse(deal1.creationTimeStamp)); // Descending
          }
        });
    return deals;
  }

  /** Sorts deals based on hot score (Highest to lowest) */
  private double epochSeconds(String timestamp) {
    LocalDateTime time = LocalDateTime.parse(timestamp);
    long epoch = time.atZone(ZoneId.of(LOCATION)).toEpochSecond();
    return epoch;
  }

  /**
   * Calculates a hot score for each deal entity, which takes into account both the time and the
   * amount of votes it got
   */
  private double calculateHotScore(Deal deal) {
    int netVotes = voteManager.getVotes(deal.id);
    double order = Math.log(Math.max(Math.abs(netVotes), 1));
    int sign = 0;
    if (netVotes > 0) sign = 1;
    else if (netVotes < 0) sign = -1;
    double seconds = epochSeconds((String) deal.creationTimeStamp) - OLDEST_DEAL_TIMESTAMP;
    return sign * order + seconds / 45000;
  }

  private List<Deal> getTrendingDeals() {
    List<Deal> allDeals = dealManager.getAllDeals();
    List<Map<String, Object>> dealWithHotScoreMaps = new ArrayList<>();
    for (Deal deal : allDeals) {
      Map<String, Object> dealWithHotScoreMap = new HashMap<>();
      dealWithHotScoreMap.put("hotScore", calculateHotScore(deal));
      dealWithHotScoreMap.put("deal", deal);
      dealWithHotScoreMaps.add(dealWithHotScoreMap);
    }
    return sortDealMapsBasedOnValue(dealWithHotScoreMaps, "hotScore");
  }

  /** Sorts deals based on votes (Highest to lowest) */
  @Override
  public List<Deal> sortDealsBasedOnVotes(List<Deal> deals) {
    List<Map<String, Object>> dealWithVotesMaps = new ArrayList<>();
    // Creates a list of maps with votes as an attribute to be sorted
    for (Deal deal : deals) {
      Map<String, Object> dealWithVotesMap = new HashMap<>();
      dealWithVotesMap.put("votes", voteManager.getVotes(deal.id));
      dealWithVotesMap.put("deal", deal);
      dealWithVotesMaps.add(dealWithVotesMap);
    }
    return sortDealMapsBasedOnValue(dealWithVotesMaps, "votes");
  }

  /** Method to sort a list of maps based on a value and return a list of deals */
  private List<Deal> sortDealMapsBasedOnValue(
      List<Map<String, Object>> dealMaps, String attribute) {
    Collections.sort(
        dealMaps,
        new Comparator<Map<String, Object>>() {
          @Override
          public int compare(Map<String, Object> deal1, Map<String, Object> deal2) {
            if (attribute.equals("hotScore")) // comparing hot score (double values)
            return -Double.compare(
                  (double) deal1.get(attribute), (double) deal2.get(attribute)); // Descending
            else // Comparing votes
            return (int) deal2.get(attribute) - (int) deal1.get(attribute); // Descending
          }
        });
    List<Deal> dealResults = new ArrayList<>(); // creating list of deals
    for (Map<String, Object> dealMap : dealMaps) {
      dealResults.add((Deal) dealMap.get("deal"));
    }
    return dealResults;
  }
}
