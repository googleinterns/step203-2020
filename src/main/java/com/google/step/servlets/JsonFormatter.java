package com.google.step.servlets;

import com.google.gson.Gson;
import com.google.step.model.Comment;
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
  public static String getCommentsWithTokenJson(
      List<Comment> comments, List<User> users, String token) {
    List<Map<String, Object>> commentMapList = new ArrayList<>();
    for (int i = 0; i < comments.size(); i++) {
      Comment comment = comments.get(i);
      User user = users.get(i);
      commentMapList.add(getCommentMap(comment, user));
    }

    Map<String, Object> commentsAndTokenMap = new HashMap<>();
    commentsAndTokenMap.put("comments", commentMapList);
    commentsAndTokenMap.put("token", token);

    Gson gson = new Gson();
    String json = gson.toJson(commentsAndTokenMap);
    return json;
  }

  public static String getCommentJson(Comment comment, User user) {
    Gson gson = new Gson();
    String json = gson.toJson(getCommentMap(comment, user));
    return json;
  }

  public static String getDealJson(
      Deal deal, Restaurant restaurant, User poster, List<Tag> tags, int votes) {
    Gson gson = new Gson();
    String json = gson.toJson(getDealMap(deal, restaurant, poster, tags, votes));
    return json;
  }

  public static String getRestaurantJson(
      Restaurant restaurant, List<Deal> deals, List<String> placeIds) {
    Gson gson = new Gson();
    String json = gson.toJson(getRestaurantMap(restaurant, deals, placeIds));
    return json;
  }

  public static String getRestaurantListBriefJson(List<Restaurant> restaurants) {
    Gson gson = new Gson();
    String json = gson.toJson(getRestaurantBriefMapsList(restaurants));
    return json;
  }

  private static List<Map<String, Object>> getRestaurantBriefMapsList(
      List<Restaurant> restaurants) {
    List<Map<String, Object>> restaurantsMaps = new ArrayList<>();
    for (Restaurant restaurant : restaurants) {
      restaurantsMaps.add(getRestaurantBriefMap(restaurant));
    }
    return restaurantsMaps;
  }

  public static String getDealListJson(List<Deal> deals) {
    Gson gson = new Gson();
    String json = gson.toJson(getDealListBriefMaps(deals));
    return json;
  }

  private static Map<String, Object> getCommentMap(Comment comment, User poster) {
    Map<String, Object> commentMap = new HashMap<>();
    commentMap.put("id", comment.id);
    commentMap.put("dealId", comment.dealId);
    commentMap.put("user", getUserBriefMap(poster));
    commentMap.put("content", comment.content);
    commentMap.put("timestamp", comment.timestamp);
    return commentMap;
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
    dealMap.put("restaurant", getRestaurantBriefMap(restaurant));
    dealMap.put("tags", getTagListBriefMaps(tags));
    dealMap.put("votes", votes);
    return dealMap;
  }

  private static Map<String, Object> getBriefDealMap(Deal deal) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("image", getImageUrl(deal.photoBlobkey));
    return dealMap;
  }

  private static Map<String, Object> getRestaurantMap(
      Restaurant restaurant, List<Deal> deals, List<String> placeIds) {
    Map<String, Object> restaurantMap = new HashMap<>();
    restaurantMap.put("id", restaurant.id);
    restaurantMap.put("name", restaurant.name);
    restaurantMap.put("photoUrl", restaurant.photoUrl);
    restaurantMap.put("deals", getDealListBriefMaps(deals));
    restaurantMap.put("placeIds", placeIds);
    return restaurantMap;
  }

  private static Map<String, Object> getRestaurantBriefMap(Restaurant restaurant) {
    Map<String, Object> restaurantMap = new HashMap<>();
    restaurantMap.put("id", restaurant.id);
    restaurantMap.put("name", restaurant.name);
    restaurantMap.put("photoUrl", restaurant.photoUrl);
    return restaurantMap;
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
      userMap.put("picture", getImageUrl(user.photoBlobKey.get()));
    } else {
      userMap.put("picture", "/images/default-profile-pic.svg");
    }

    userMap.put("dealsUploaded", getDealListBriefMaps(deals));
    userMap.put("following", getUserListBriefMaps(following));
    userMap.put("followers", getUserListBriefMaps(followers));
    userMap.put("tagsFollowed", getTagListBriefMaps(tags));
    userMap.put("restaurantsFollowed", getRestaurantBriefMapsList(restaurants));
    return userMap;
  }

  /**
   * Returns a map representation of brief user info.
   *
   * @param user the user object being formatted.
   * @return a map representation of brief user info.
   */
  private static Map<String, Object> getUserBriefMap(User user) {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("id", user.id);
    userMap.put("username", user.username);
    if (user.photoBlobKey.isPresent()) {
      userMap.put("picture", getImageUrl(user.photoBlobKey.get()));
    } else {
      userMap.put("picture", "/images/default-profile-pic.svg");
    }
    return userMap;
  }

  /**
   * Returns a list of maps of brief user info.
   *
   * @param users a list of users whose brief info will be returned
   * @return a list of maps of brief user info.
   */
  private static List<Map<String, Object>> getUserListBriefMaps(List<User> users) {
    List<Map<String, Object>> userMaps = new ArrayList<>();
    for (User user : users) {
      userMaps.add(getUserBriefMap(user));
    }
    return userMaps;
  }

  /**
   * Returns a list of maps of brief tag info.
   *
   * @param tags a list of tags.
   * @return a list of maps of brief tag info.
   */
  private static List<Map<String, Object>> getTagListBriefMaps(List<Tag> tags) {
    List<Map<String, Object>> tagMaps = new ArrayList<>();
    for (Tag tag : tags) {
      tagMaps.add(getTagBriefMap(tag));
    }
    return tagMaps;
  }

  /**
   * Returns a map representation of brief tag info.
   *
   * @param tag a tag object.
   * @return a map representation of brief tag info.
   */
  private static Map<String, Object> getTagBriefMap(Tag tag) {
    Map<String, Object> tagMap = new HashMap<>();
    tagMap.put("id", tag.id);
    tagMap.put("name", tag.name);
    return tagMap;
  }

  private static String getImageUrl(String blobKey) {
    return "/api/images/" + blobKey;
  }

  public static String getHomePageJson(Map<String, Object> homePageDealsMaps) {
    Gson gson = new Gson();
    String json = gson.toJson(homePageDealsMaps);
    return json;
  }

  /**
   * Returns a json of home page section data.
   *
   * @param homePageSectionDealsMaps a list of deal maps for a home page section
   * @return a json of home page section data
   */
  public static String getHomePageSectionJson(List<Map<String, Object>> homePageSectionDealsMaps) {
    Gson gson = new Gson();
    String json = gson.toJson(homePageSectionDealsMaps);
    return json;
  }

  /**
   * Returns a map of brief deal info for home page
   *
   * @param deal the deal to create a map for
   * @param poster the user object of the poster
   * @param restaurant restaurant the deal is at
   * @param tags tags associated with the deal
   * @param votes number of votes the deal received
   * @return a map of brief deal info for home page
   */
  public static Map<String, Object> getBriefHomePageDealMap(
      Deal deal, User poster, Restaurant restaurant, List<Tag> tags, int votes) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("pic", getImageUrl(deal.photoBlobkey));
    dealMap.put("poster", getUserBriefMap(poster));
    dealMap.put("restaurant", getRestaurantBriefMap(restaurant));
    dealMap.put("votes", votes);
    dealMap.put("tags", getTagListBriefMaps(tags));
    dealMap.put("timestamp", deal.creationTimeStamp);
    return dealMap;
  }
}
