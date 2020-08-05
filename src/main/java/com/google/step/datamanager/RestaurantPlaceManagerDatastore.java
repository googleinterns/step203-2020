package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantPlaceManagerDatastore implements RestaurantPlaceManager {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void updatePlacesOfRestaurant(long restaurantId, List<String> placeIds) {
    Set<String> newPlaceIds = new HashSet<>(placeIds);
    Iterable<Entity> currentPlaceEntities = getRestaurantPlaceEntities(restaurantId);

    // Keeps place ids in the new list and remove others
    for (Entity entity : currentPlaceEntities) {
      String placeId = (String) entity.getProperty("placeId");
      if (newPlaceIds.contains(placeId)) {
        newPlaceIds.remove(placeId);
        continue;
      }
      datastore.delete(entity.getKey());
    }

    // Adds remaining place ids in the new list
    for (String placeId : newPlaceIds) {
      Entity entity = createRestaurantPlaceEntity(restaurantId, placeId);
      datastore.put(entity);
    }
  }

  @Override
  public Set<String> getPlaceIdsOfRestaurant(long restaurantId) {
    Iterable<Entity> results = getRestaurantPlaceEntities(restaurantId);
    Set<String> placeIds = new HashSet<>();
    for (Entity entity : results) {
      placeIds.add((String) entity.getProperty("placeId"));
    }

    return placeIds;
  }

  private Entity createRestaurantPlaceEntity(long restaurantId, String placeId) {
    Entity entity = new Entity("RestaurantPlace");
    entity.setProperty("restaurantId", restaurantId);
    entity.setProperty("placeId", placeId);
    return entity;
  }

  private Iterable<Entity> getRestaurantPlaceEntities(long restaurantId) {
    Query query = new Query("RestaurantPlace");
    Filter filter = new FilterPredicate("restaurantId", FilterOperator.EQUAL, restaurantId);
    query.setFilter(filter);
    return datastore.prepare(query).asIterable();
  }

  @Override
  public void deletePlacesOfRestaurant(long restaurantId) {
    updatePlacesOfRestaurant(restaurantId, new ArrayList<>());
  }
}
