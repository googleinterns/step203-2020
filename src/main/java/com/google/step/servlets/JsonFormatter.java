package com.google.step.servlets;

import com.google.gson.Gson;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A class that handles converting entities to json format. */
public class JsonFormatter {
  public static String getDealJson(
      Deal deal, Restaurant restaurant, User poster, List<Tag> tags, int votes) {
    Gson gson = new Gson();
    String json = gson.toJson(getDealMap(deal, restaurant, poster, tags, votes));
    return json;
  }

  private static Map<String, Object> getDealMap(
      Deal deal, Restaurant restaurant, User poster, List<Tag> tags, int votes) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("image", getImageUrl(deal.photoBlobkey));
    dealMap.put("start", deal.start.toString());
    dealMap.put("end", deal.end.toString());
    dealMap.put("source", deal.source);
    dealMap.put("poster", getUserBriefMap(poster));
    dealMap.put("restaurant", restaurant.name); // TODO use restaurant brief
    dealMap.put("tags", getTagListBriefMaps(tags));
    dealMap.put("votes", votes);
    return dealMap;
  }

  private static Map<String, Object> getBriefDealMap(Deal deal) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("image", getImageUrl(deal.photoBlobkey));
    dealMap.put("poster", deal.posterId); // TODO use user name
    dealMap.put("restaurant", deal.restaurantId); // TODO use restaurant name
    dealMap.put("votes", 0); // TODO add votes
    return dealMap;
  }

  private static List<Map<String, Object>> getDealListBriefMaps(List<Deal> deals) {
    List<Map<String, Object>> dealMaps = new ArrayList<>();
    for (Deal deal : deals) {
      dealMaps.add(getBriefDealMap(deal));
    }
    return dealMaps;
  }

  /**
   * Returns json string representation of a user object.
   *
   * @param user The user object to be formatted.
   * @return json string representation of a user object.
   */
  public static String getUserJson(
      User user,
      List<Deal> deals,
      List<User> following,
      List<User> followers,
      List<Tag> tags,
      List<Restaurant> restaurants) {
    Gson gson = new Gson();
    String json = gson.toJson(getUserMap(user, deals, following, followers, tags, restaurants));
    return json;
  }

  /**
   * Returns a map representation of user properties and values.
   *
   * @param user the user object being formatted
   * @param deals deals published by the user
   * @param following users followed by the user
   * @param followers followers of the user
   * @param tags tags followed by the user
   * @param restaurants restaurants followed by the user
   * @return a map representation of user properties and values.
   */
  private static Map<String, Object> getUserMap(
      User user,
      List<Deal> deals,
      List<User> following,
      List<User> followers,
      List<Tag> tags,
      List<Restaurant> restaurants) {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("id", user.id);
    userMap.put("username", user.username);
    userMap.put("email", user.email);
    userMap.put("bio", user.bio);
    if (user.photoBlobKey.isPresent()) {
      userMap.put("photoBlobKey", user.photoBlobKey.get());
    }

    userMap.put("dealsUploaded", getDealListBriefMaps(deals));
    userMap.put("following", getUserListBriefMaps(following));
    userMap.put("followers", getUserListBriefMaps(followers));
    userMap.put("tagsFollowed", getTagListBriefMaps(tags));
    userMap.put("restaurantsFollowed", getRestaurantListBriefMaps(restaurants));
    return userMap;
  }

  /**
   * Returns a map representation of brief user info.
   *
   * @param user the user object being formatted
   * @return a map representation of brief user info.
   */
  private static Map<String, Object> getUserBriefMap(User user) {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("id", user.id);
    userMap.put("username", user.username);
    if (user.photoBlobKey.isPresent()) {
      userMap.put("photoBlobKey", user.photoBlobKey.get());
    }
    return userMap;
  }

  /**
   * Returns a list of maps of brief user info.
   *
   * @param users a list of user whose brief info will be returned
   * @return a list of maps of brief user info.
   */
  private static List<Map<String, Object>> getUserListBriefMaps(List<User> users) {
    List<Map<String, Object>> userMaps = new ArrayList<>();
    for (User user : users) {
      userMaps.add(getUserBriefMap(user));
    }
    return userMaps;
  }

  // TODO
  private static List<Map<String, Object>> getTagListBriefMaps(List<Tag> tags) {
    return new ArrayList<>();
  }

  // TODO
  private static List<Map<String, Object>> getRestaurantListBriefMaps(
      List<Restaurant> restaurants) {
    return new ArrayList<>();
  }

  private static String getImageUrl(String blobKey) {
    return "/api/images/" + blobKey;
  }
}
