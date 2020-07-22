package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantPlaceManagerDatastore implements RestaurantPlaceManager {

  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void updatePlacesOfRestaurants(long restaurantId, List<String> placeIds) {
    Set<String> newPlaceIds = new HashSet<>(placeIds);
    Iterable<Entity> results = getEntitiesOfRestaurant(restaurantId);

    // Keeps place ids in the new list and remove others
    for (Entity entity : results) {
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
  public Set<String> getPlaceIdsOfRestaurant(long id) {
    Iterable<Entity> results = getEntitiesOfRestaurant(id);
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

  private Iterable<Entity> getEntitiesOfRestaurant(long id) {
    Query query = new Query("RestaurantPlace");
    Filter filter = new FilterPredicate("restaurantId", FilterOperator.EQUAL, id);
    query.setFilter(filter);
    Iterable<Entity> results = datastore.prepare(query).asIterable();
    return results;
  }
}
