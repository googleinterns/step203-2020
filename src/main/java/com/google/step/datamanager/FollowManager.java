package com.google.step.datamanager;

import java.util.List;

public interface FollowManager {
  public void followRestaurant(long followerId, long restaurantId);

  public void unfollowRestaurant(long followerId, long restaurantId);

  public List<Long> getFollowedRestaurants(long followerId);

  public void followUser(long followerId, long userId);

  public void unfollowUser(long followerId, long userId);

  public List<Long> getFollowedUsers(long followerId);

  public void followTag(long followerId, long tagId);

  public void unfollowTag(long followerId, long tagId);

  public List<Long> getFollowedTags(long followerId);
}
