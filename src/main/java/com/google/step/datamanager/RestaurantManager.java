package com.google.step.datamanager;

import com.google.step.model.Restaurant;

public interface RestaurantManager {

  public Restaurant createRestaurant(String name, String photoBlobkey);

  public Restaurant readRestaurant(long id);

  public Restaurant updateRestaurant(Restaurant restaurant);

  public void deleteRestaurant(long id);
  
}
