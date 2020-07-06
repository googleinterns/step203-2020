package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.step.model.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantManagerDatastore implements RestaurantManager {

  private final DatastoreService datastore;

  public RestaurantManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Restaurant createRestaurant(String name, String photoBlobkey) {
    Entity entity = new Entity("Restaurant");
    entity.setProperty("name", name);
    entity.setProperty("photoBlobkey", photoBlobkey);
    entity.setProperty("name_lowercase", name.toLowerCase());

    Key key = datastore.put(entity);
    long id = key.getId();

    Restaurant restaurant = new Restaurant(id, name, photoBlobkey);

    return restaurant;
  }

  @Override
  public Restaurant readRestaurant(long id) {
    Key key = KeyFactory.createKey("Restaurant", id);
    Entity restaurantEntity;
    try {
      restaurantEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }

    return transformEntityToRestaurant(restaurantEntity);
  }

  @Override
  public Restaurant updateRestaurant(Restaurant restaurant) {
    Key key = KeyFactory.createKey("Restaurant", restaurant.id);
    Entity restaurantEntity;
    try {
      restaurantEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    if (restaurant.name != null) {
      restaurantEntity.setProperty("name", restaurant.name);
      restaurantEntity.setProperty("name_lowercase", restaurant.name.toLowerCase());
    }
    if (restaurant.photoBlobkey != null) {
      restaurantEntity.setProperty("photoBlobkey", restaurant.photoBlobkey);
    }
    datastore.put(restaurantEntity);
    return readRestaurant(restaurant.id);
  }

  @Override
  public void deleteRestaurant(long id) {
    Key key = KeyFactory.createKey("Restaurant", id);
    datastore.delete(key);
  }

  @Override
  public List<Restaurant> searchRestaurant(String queryString) {
    queryString = queryString.toLowerCase();
    Filter filterPrefix =
        CompositeFilterOperator.and(
            FilterOperator.GREATER_THAN_OR_EQUAL.of("name_lowercase", queryString),
            FilterOperator.LESS_THAN.of("name_lowercase", queryString + "~"));
    Query query = new Query("Restaurant").setFilter(filterPrefix);
    PreparedQuery preparedQuery = datastore.prepare(query);

    List<Restaurant> restaurants = new ArrayList<>();
    for (Entity entity : preparedQuery.asIterable()) {
      restaurants.add(transformEntityToRestaurant(entity));
    }

    return restaurants;
  }

  /**
   * Returns a Restaurant object transformed from a restaurant entity.
   *
   * @param entity Restaurant entity.
   * @return a Restaurant object transformed from the entity.
   */
  private Restaurant transformEntityToRestaurant(Entity entity) {
    String name = (String) entity.getProperty("name");
    String photoBlobkey = (String) entity.getProperty("photoBlobkey");
    Restaurant restaurant = new Restaurant(entity.getKey().getId(), name, photoBlobkey);
    return restaurant;
  }
}
