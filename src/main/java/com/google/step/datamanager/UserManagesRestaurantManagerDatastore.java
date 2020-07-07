package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.util.ArrayList;
import java.util.List;

public class UserManagesRestaurantManagerDatastore implements UserManagesRestaurantManager {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void addUserManagesRestaurant(long userId, long restaurantId) {
    Entity entity = getUserRestaurantEntity(userId, restaurantId);
    if (entity != null) {
      return;
    }

    entity = new Entity("UserRestaurant");
    entity.setProperty("userId", userId);
    entity.setProperty("restaurantId", restaurantId);

    datastore.put(entity);
  }

  @Override
  public void deleteUserManagesRestaurant(long userId, long restaurantId) {
    Entity entity = getUserRestaurantEntity(userId, restaurantId);

    if (entity != null) {
      datastore.delete(entity.getKey());
    }
  }

  /**
   * Returns a UserRestaurant entity with the given user id and restaurant id.
   *
   * @param userId id of user
   * @param restaurantId id of restaurant
   * @return a UserRestaurant entity with the given user id and restaurant id.
   */
  private Entity getUserRestaurantEntity(long userId, long restaurantId) {
    Filter filter =
        CompositeFilterOperator.and(
            new Query.FilterPredicate("userId", FilterOperator.EQUAL, userId),
            new Query.FilterPredicate("restaurantId", FilterOperator.EQUAL, restaurantId));
    Query query = new Query("UserRestaurant").setFilter(filter);
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    return entity;
  }

  @Override
  public List<Long> getManagerIdsOfRestaurant(long id) {
    Query query =
        new Query("UserRestaurant")
            .setFilter(new FilterPredicate("restaurantId", FilterOperator.EQUAL, id));

    Iterable<Entity> results = datastore.prepare(query).asIterable();
    List<Long> managerIds = new ArrayList<>();
    for (Entity entity : results) {
      long userId = (long) entity.getProperty("userId");
      managerIds.add(userId);
    }
    return managerIds;
  }

  @Override
  public List<Long> getRestaurantIdsManagedBy(long id) {
    Query query =
        new Query("UserRestaurant")
            .setFilter(new FilterPredicate("userId", FilterOperator.EQUAL, id));

    Iterable<Entity> results = datastore.prepare(query).asIterable();
    List<Long> restaurantIds = new ArrayList<>();
    for (Entity entity : results) {
      long restaurantId = (long) entity.getProperty("restaurantId");
      restaurantIds.add(restaurantId);
    }
    return restaurantIds;
  }
}
