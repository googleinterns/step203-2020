package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.step.model.Restaurant;

public class RestaurantManagerDatastore implements RestaurantManager {

  private final DatastoreService datastore;

  public RestaurantManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Restaurant createRestaurant(
      String name,
      String photoBlobkey) {
    Entity entity = new Entity("Restaurant");
    entity.setProperty("name", description);
    entity.setProperty("photoBlobkey", photoBlobkey);

    Key key = datastore.put(entity);
    long id = key.getId();

    Restaurant restaurant = new Deal(id, name, photoBlobkey);

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
    String name = (String) restaurantEntity.getProperty("name");
    String photoBlobkey = (String) restaurantEntity.getProperty("photoBlobkey");
    Restaurant restaurant = new Restaurant(id, name, photoBlobkey);
    return restaurant;
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
    }
    if (restaurant.photoBlobkey != null) {
      restaurantEntity.setProperty("photoBlobkey", restaurant.photoBlobkey);
    }
    datastore.put(restaurantEntity);
    return readRestaurant(restaurant.id);
  }
}
