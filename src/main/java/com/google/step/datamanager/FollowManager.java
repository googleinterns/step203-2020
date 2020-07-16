package com.google.step.datamanager;

import java.util.List;
import java.util.Set;

public interface FollowManager {
  public void followRestaurant(long followerId, long restaurantId);

  public void unfollowRestaurant(long followerId, long restaurantId);

  public Set<Long> getFollowedRestaurantIds(long followerId);

  public void followUser(long followerId, long userId);

  public void unfollowUser(long followerId, long userId);

  public Set<Long> getFollowedUserIds(long followerId);

  public void followTag(long followerId, long tagId);

  public void unfollowTag(long followerId, long tagId);

  public Set<Long> getFollowedTagIds(long followerId);

  public void updateFollowedTagIds(long followerId, List<Long> tagIds);
}
