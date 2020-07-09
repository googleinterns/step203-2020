package com.google.step.datamanager;

import java.util.List;

public interface FollowManager {
  public void followRestaurant(long followerId, long restaurantId);

  public void unfollowRestaurant(long followerId, long restaurantId);

  public List<Long> getFollowedRestaurantIds(long followerId);

  public void followUser(long followerId, long userId);

  public void unfollowUser(long followerId, long userId);

  public List<Long> getFollowedUserIds(long followerId);

  public List<Long> getFollowerIdsOfUser(long followeeId);

  public void followTag(long followerId, long tagId);

  public void unfollowTag(long followerId, long tagId);

  public List<Long> getFollowedTagIds(long followerId);
}
