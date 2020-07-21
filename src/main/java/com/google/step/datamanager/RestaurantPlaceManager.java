package com.google.step.datamanager;

import java.util.List;
import java.util.Set;

public interface RestaurantPlaceManager {
  public void updatePlacesOfRestaurants(long restaurantId, List<String> placeIds);

  public Set<String> getPlaceIdsOfRestaurant(long id);
}
