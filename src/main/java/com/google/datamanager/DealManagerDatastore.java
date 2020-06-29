package com.google.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.model.Deal;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;

  public DealManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long poster,
      long restaurant) {
    Entity entity = new Entity("Deal");
    entity.setProperty("description", description);
    entity.setProperty("photoBlobkey", photoBlobkey);
    entity.setProperty("start", start);
    entity.setProperty("end", end);
    entity.setProperty("source", source);
    entity.setProperty("poster", poster);
    entity.setProperty("restaurant", restaurant);

    Key key = datastore.put(entity);
    long id = key.getId();

    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, poster, restaurant);

    return deal;
  }

  @Override
  public Deal readDeal(long id) {
    Key key = KeyFactory.createKey("Deal", id);
    Entity dealEntity;
    try {
      dealEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    String description = (String) dealEntity.getProperty("description");
    String photoBlobkey = (String) dealEntity.getProperty("photoBlobkey");
    String start = (String) dealEntity.getProperty("start");
    String end = (String) dealEntity.getProperty("end");
    String source = (String) dealEntity.getProperty("source");
    long poster = (long) dealEntity.getProperty("poster");
    long restaurant = (long) dealEntity.getProperty("restaurant");
    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, poster, restaurant);
    return deal;
  }

  @Override
  public void deleteDeal(long id) {
    Key key = KeyFactory.createKey("Deal", id);
    datastore.delete(key);
  }

  @Override
  public Deal updateDeal(Deal deal) {
    // TODO Auto-generated method stub
    return null;
  }
}
