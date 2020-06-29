package com.google.step.datamanager;

public interface FollowManager {
  public void followRestaurant(long followerId, long restaurantId);

  public void unfollowRestaurant(long followerId, long restaurantId);

  public void followUser(long followerId, long userId);

  public void unfollowUser(long followerId, long userId);

  public void followTag(long followerId, long tagId);

  public void unfollowTag(long followerId, long tagId);
}
