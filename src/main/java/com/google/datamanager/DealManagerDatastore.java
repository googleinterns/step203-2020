package com.google.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteDeal(long id) {
    // TODO Auto-generated method stub

  }

  @Override
  public Deal updateDeal(Deal deal) {
    // TODO Auto-generated method stub
    return null;
  }
}
