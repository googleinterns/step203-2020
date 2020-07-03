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
}
