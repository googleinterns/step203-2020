package com.google.step.datamanager;

import com.google.step.model.Restaurant;
import java.util.List;

public interface RestaurantManager {

  public Restaurant createRestaurant(String name, String photoBlobkey);

  public Restaurant readRestaurant(long id);

  public Restaurant updateRestaurant(Restaurant restaurant);

  public void deleteRestaurant(long id);

  public List<Restaurant> readRestaurants(List<Long> ids);
}
