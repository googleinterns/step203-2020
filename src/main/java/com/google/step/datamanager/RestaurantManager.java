package com.google.step.datamanager;

import com.google.step.model.Restaurant;
import java.util.List;

public interface RestaurantManager {

  public Restaurant createRestaurantWithBlobKey(String name, String photoBlobkey, long posterId);

  public Restaurant createRestaurantWithPhotoReference(String name, String photoUrl, long posterId);

  public Restaurant readRestaurant(long id);

  public Restaurant updateRestaurant(Restaurant restaurant);

  public void deleteRestaurant(long id);

  public void deleteAllRestaurants();

  public List<Restaurant> getAllRestaurants();

  public List<Restaurant> readRestaurants(List<Long> ids);

  /** Finds restaurant whose name starts with {@code query}. */
  public List<Restaurant> searchRestaurants(String query);
}
