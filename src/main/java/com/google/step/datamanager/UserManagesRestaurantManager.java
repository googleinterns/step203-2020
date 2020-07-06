package com.google.step.datamanager;

import java.util.List;

public interface UserManagesRestaurantManager {
  /**
   * Adds a user to manage the restaurant.
   *
   * @param userId id of the user.
   * @param restaurantId id of the restaurant.
   */
  public void addUserManagesRestaurant(long userId, long restaurantId);

  /**
   * Deletes a user managing restaurant relation.
   *
   * @param userId id of the user.
   * @param restaurantId id of the restaurant.
   */
  public void deleteUserManagesRestaurant(long userId, long restaurantId);

  /**
   * Returns ids of users who manages the restaurant with the id.
   *
   * @param id id of the user.
   * @return ids of users who manages the restaurant with the id.
   */
  public List<Long> getManagerIdsOfRestaurant(long id);

  /**
   * Returns ids of restaurants managed by the user with the id.
   *
   * @param id id of the user.
   * @return ids of restaurants managed by the user with the id.
   */
  public List<Long> getRestaurantIdsManagedBy(long id);
}
