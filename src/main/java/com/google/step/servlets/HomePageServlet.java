package com.google.step.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.CommentManagerDatastore;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealManagerDatastore;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealTagManagerDatastore;
import com.google.step.datamanager.DealVoteCountManager;
import com.google.step.datamanager.DealVoteCountManagerDatastore;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.FollowManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.TagManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles retrieving deals for home page */
@WebServlet("/api/home")
public class HomePageServlet extends HttpServlet {

  private final DealManager dealManager;
  private final UserManager userManager;
  private final RestaurantManager restaurantManager;
  private final DealTagManager dealTagManager;
  private final UserService userService;
  private final TagManager tagManager;
  private final FollowManager followManager;
  private final DealVoteCountManager dealVoteCountManager;
  private final CommentManager commentManager;
  private final Long OLDEST_DEAL_TIMESTAMP = 1594652120L; // arbitrary datetime of first deal posted
  private final String LOCATION = "Asia/Singapore";

  private static final String TRENDING = "trending";
  private static final String USERS_SECTION = "users";
  private static final String RESTAURANTS_SECTION = "restaurants";
  private static final String TAGS_SECTION = "tags";

  private static final String VOTE_SORT = "votes";
  private static final String NEW_SORT = "new";

  private static final int NUM_HOMEPAGE_DEALS = 8;

  public HomePageServlet(
      DealManager dealManager,
      UserManager userManager,
      RestaurantManager restaurantManager,
      DealTagManager dealTagManager,
      TagManager tagManager,
      FollowManager followManager,
      DealVoteCountManager dealVoteCountManager,
      CommentManager commentManager,
      UserService userService) {
    this.dealManager = dealManager;
    this.userManager = userManager;
    this.restaurantManager = restaurantManager;
    this.dealTagManager = dealTagManager;
    this.tagManager = tagManager;
    this.followManager = followManager;
    this.dealVoteCountManager = dealVoteCountManager;
    this.commentManager = commentManager;
    this.userService = userService;
  }

  public HomePageServlet() {
    dealManager = new DealManagerDatastore();
    userManager = new UserManagerDatastore();
    restaurantManager = new RestaurantManagerDatastore();
    tagManager = new TagManagerDatastore();
    dealTagManager = new DealTagManagerDatastore();
    followManager = new FollowManagerDatastore();
    dealVoteCountManager = new DealVoteCountManagerDatastore();
    commentManager = new CommentManagerDatastore();
    userService = UserServiceFactory.getUserService();
  }

  /** Class to store deal along with relevant attribute (hot score/votes) to be sorted */
  class ScoredDeal implements Comparable<ScoredDeal> {
    public final Deal deal;
    public final double score;

    public ScoredDeal(Deal deal, double score) {
      this.deal = deal;
      this.score = score;
    }

    @Override
    public int compareTo(ScoredDeal other) {
      return -Double.compare(score, other.score);
    }
  }

  /** Gets the deals for the home page/ view all deals */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String homePageSection = request.getParameter("section");
    String sort = request.getParameter("sort");
    if (homePageSection != null
        && !homePageSection.equals(TRENDING)
        && !homePageSection.equals(USERS_SECTION)
        && !homePageSection.equals(RESTAURANTS_SECTION)
        && !homePageSection.equals(TAGS_SECTION)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (sort != null
        && !sort.equals(TRENDING)
        && !sort.equals(VOTE_SORT)
        && !sort.equals(NEW_SORT)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    // if no home page section is being specified to view all deals, return home page data
    if (userService.isUserLoggedIn()) { // all sections are available
      response.setContentType("application/json;");
      response.getWriter().println(userLoggedIn(homePageSection, sort));
    } else {
      String result = userNotLoggedIn(homePageSection);
      if (result != null) {
        response.setContentType("application/json;");
        response.getWriter().println(result);
      } else {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
  }

  private String userLoggedIn(String homePageSection, String sort) {
    String email = userService.getCurrentUser().getEmail();
    User user = userManager.readUserByEmail(email);
    long userId = user.id;
    if (homePageSection == null) {
      List<List<Map<String, Object>>> homePageDealsMaps =
          getSectionListMaps(homePageSection, userId, NUM_HOMEPAGE_DEALS, sort);
      Map<String, Object> homePageMap = new HashMap<>();
      homePageMap.put(TRENDING, homePageDealsMaps.get(0));
      homePageMap.put(USERS_SECTION, homePageDealsMaps.get(1));
      homePageMap.put(RESTAURANTS_SECTION, homePageDealsMaps.get(2));
      homePageMap.put(TAGS_SECTION, homePageDealsMaps.get(3));
      return JsonFormatter.getHomePageJson(homePageMap);
      // user requested to view all deals of particular section
    } else {
      List<List<Map<String, Object>>> homePageDealsMaps =
          getSectionListMaps(homePageSection, userId, -1, sort);
      return JsonFormatter.getHomePageSectionJson(homePageDealsMaps.get(0));
    }
  }

  private String userNotLoggedIn(String homePageSection) {
    if (homePageSection == null) { // only trending will be shown when not logged in
      List<List<Map<String, Object>>> homePageDealsMaps =
          getSectionListMaps(TRENDING, -1, NUM_HOMEPAGE_DEALS, null);
      Map<String, Object> homePageMap = new HashMap<>();
      homePageMap.put(TRENDING, homePageDealsMaps.get(0));
      return JsonFormatter.getHomePageJson(homePageMap);
    } else if (homePageSection.equals(TRENDING)) {
      // User views all deals for trending section
      List<List<Map<String, Object>>> homePageDealsMaps =
          getSectionListMaps(homePageSection, -1, -1, null);
      return JsonFormatter.getHomePageSectionJson(homePageDealsMaps.get(0));
    } else { // user is unable to view all deals for other sections when not logged in
      return null;
    }
  }

  /** Gets a list of list of maps based on the required section(s) */
  private List<List<Map<String, Object>>> getSectionListMaps(
      String section, long userId, int limit, String sort) {
    List<List<Map<String, Object>>> totalDealMaps = new ArrayList<>();
    // for trending section, there is no sorting available as it is already sorted by trending
    if (section == null || section.equals(TRENDING)) {
      List<Deal> allDeals = dealManager.getAllDeals();
      List<Deal> trendingDeals = sortDealsBasedOnHotScore(allDeals);
      if (limit > 0) {
        trendingDeals = trendingDeals.stream().limit(limit).collect(Collectors.toList());
      }
      totalDealMaps.add(getHomePageSectionMap(trendingDeals));
    }
    List<Long> dealIds = null;
    List<Deal> deals = null;
    if (section == null || section.equals(USERS_SECTION)) {
      Set<Long> userIds = followManager.getFollowedUserIds(userId);
      if (sort == null || sort.equals(NEW_SORT)) {
        dealIds = dealManager.getDealsPublishedByUsersSortByNew(userIds, limit);
        deals = sort == null ? dealManager.readDeals(dealIds) : dealManager.readDealsOrder(dealIds);
      } else { // need to retrieve all deals first, then sort in this servlet
        dealIds = dealManager.getDealsPublishedByUsers(userIds, -1);
        deals = handleSortVoteTrending(dealIds, limit, sort);
      }
      totalDealMaps.add(getHomePageSectionMap(deals));
    }
    if (section == null || section.equals(RESTAURANTS_SECTION)) {
      Set<Long> restaurantIds = followManager.getFollowedRestaurantIds(userId);
      if (sort == null || sort.equals(NEW_SORT)) {
        dealIds = dealManager.getDealsPublishedByRestaurantsSortByNew(restaurantIds, limit);
        deals = sort == null ? dealManager.readDeals(dealIds) : dealManager.readDealsOrder(dealIds);
      } else {
        dealIds = dealManager.getDealsPublishedByRestaurants(restaurantIds, -1);
        deals = handleSortVoteTrending(dealIds, limit, sort);
      }
      totalDealMaps.add(getHomePageSectionMap(deals));
    }
    if (section == null || section.equals(TAGS_SECTION)) {
      Set<Long> dealIdsTags = getDealsPublishedByTags(followManager.getFollowedTagIds(userId));
      if (sort == null) {
        deals = dealManager.readDeals(new ArrayList<>(dealIdsTags));
        if (limit > 0) {
          deals = deals.stream().limit(limit).collect(Collectors.toList());
        }
      } else if (sort.equals(NEW_SORT)) {
        dealIds = dealManager.getDealsWithIdsSortByNew(dealIdsTags, limit);
        deals = dealManager.readDealsOrder(dealIds);
      } else {
        dealIds = dealManager.getDealsWithIds(dealIdsTags, -1);
        deals = handleSortVoteTrending(dealIds, limit, sort);
      }
      totalDealMaps.add(getHomePageSectionMap(deals));
    }
    return totalDealMaps;
  }

  private List<Deal> handleSortVoteTrending(List<Long> dealIds, int limit, String sort) {
    List<Deal> deals = null;
    if (sort.equals(VOTE_SORT)) {
      dealIds = dealVoteCountManager.sortDealsInOrderOfVotes(dealIds, limit);
      deals = dealManager.readDealsOrder(dealIds);
    } else if (sort.equals(TRENDING)) {
      deals = dealManager.readDeals(dealIds);
      deals = sortDealsBasedOnHotScore(deals);
      if (limit > 0) {
        deals = deals.stream().limit(limit).collect(Collectors.toList());
      }
    }
    return deals;
  }

  /** Creates a list of deal maps for a section */
  private List<Map<String, Object>> getHomePageSectionMap(List<Deal> sectionDeals) {
    List<Map<String, Object>> homePageSectionDealMaps = new ArrayList<>();
    for (Deal deal : sectionDeals) {
      User user = userManager.readUser(deal.posterId);
      Restaurant restaurant = restaurantManager.readRestaurant(deal.restaurantId);
      List<Tag> tags = tagManager.readTags(dealTagManager.getTagIdsOfDeal(deal.id));
      int votes = dealVoteCountManager.getVotes(deal.id);
      Map<String, Object> homePageDealMap =
          JsonFormatter.getBriefHomePageDealMap(deal, user, restaurant, tags, votes);
      homePageSectionDealMaps.add(homePageDealMap);
    }
    return homePageSectionDealMaps;
  }

  /** Retrieves deals posted by tags followed by user */
  private Set<Long> getDealsPublishedByTags(Set<Long> tagIds) {
    Set<Long> dealIdResults = new HashSet<>();
    for (Long id : tagIds) {
      List<Long> dealIdsWithTag = dealTagManager.getDealIdsWithTag(id);
      dealIdResults.addAll(dealIdsWithTag);
    }
    return dealIdResults;
  }

  private double epochSeconds(String timestamp) {
    LocalDateTime time = LocalDateTime.parse(timestamp);
    long epoch = time.atZone(ZoneId.of(LOCATION)).toEpochSecond();
    return epoch;
  }

  /**
   * Calculates a hot score for each deal entity, which takes into account both the time and the
   * amount of votes it got
   */
  private double calculateHotScore(Deal deal, int votes) {
    double order = Math.log(Math.max(Math.abs(votes), 1));
    int sign = 0;
    if (votes > 0) {
      sign = 1;
    } else if (votes < 0) {
      sign = -1;
    }
    double seconds = epochSeconds((String) deal.creationTimeStamp) - OLDEST_DEAL_TIMESTAMP;
    double hotScore = sign * order + seconds / 45000;
    double dealAvgSentiment = commentManager.getAvgCommentSentiment(deal.id);
    return hotScore * dealAvgSentiment;
  }

  /** Sorts deals based on hot score */
  private List<Deal> sortDealsBasedOnHotScore(List<Deal> deals) {
    List<ScoredDeal> scoredDeals = new ArrayList<>();
    for (Deal deal : deals) {
      int votes = dealVoteCountManager.getVotes(deal.id);
      scoredDeals.add(new ScoredDeal(deal, calculateHotScore(deal, votes)));
    }
    Collections.sort(scoredDeals);
    List<Deal> dealResults = new ArrayList<>(); // creating list of deals
    for (ScoredDeal scoredDeal : scoredDeals) {
      dealResults.add(scoredDeal.deal);
    }
    return dealResults;
  }
}
