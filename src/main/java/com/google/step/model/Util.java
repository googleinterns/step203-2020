package com.google.step.model;

import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealTagManagerDatastore;
import com.google.step.datamanager.DealVoteCountManager;
import com.google.step.datamanager.DealVoteCountManagerDatastore;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantManagerDatastore;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.TagManagerDatastore;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.UserManagerDatastore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Util {
  /**
   * Returns true if both objects are null or the {@code equals} method returns true. Otherwise,
   * returns false.
   */
  private static final UserManager userManager = new UserManagerDatastore();

  private static final RestaurantManager restaurantManager = new RestaurantManagerDatastore();
  private static final DealTagManager dealTagManager = new DealTagManagerDatastore();
  private static final TagManager tagManager = new TagManagerDatastore();
  private static final DealVoteCountManager dealVoteCountManager =
      new DealVoteCountManagerDatastore();

  public static boolean isEqual(Object objA, Object objB) {
    return (objA == null ? objB == null : objA.equals(objB));
  }

  public static List<Map<String, Object>> getHomePageSectionMap(List<Deal> sectionDeals) {
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
}
