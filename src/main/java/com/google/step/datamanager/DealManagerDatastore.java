package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.Deal;
import java.util.ArrayList;
import java.util.List;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;
  private final DealSearchManager searchManager;

  public DealManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    searchManager = new DealSearchManagerIndex();
  }

  @Override
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId) {
    Entity entity = new Entity("Deal");
    entity.setProperty("description", description);
    entity.setProperty("photoBlobkey", photoBlobkey);
    entity.setProperty("start", start);
    entity.setProperty("end", end);
    entity.setProperty("source", source);
    entity.setProperty("posterId", posterId);
    entity.setProperty("restaurantId", restaurantId);

    Key key = datastore.put(entity);
    long id = key.getId();

    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId);
    searchManager.putDeal(deal, new ArrayList<>());

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
    long posterId = (long) dealEntity.getProperty("posterId");
    long restaurantId = (long) dealEntity.getProperty("restaurantId");
    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId);
    return deal;
  }

  @Override
  public void deleteDeal(long id) {
    Key key = KeyFactory.createKey("Deal", id);
    datastore.delete(key);
    searchManager.removeDeal(id);
  }

  @Override
  public Deal updateDeal(Deal deal) {
    Key key = KeyFactory.createKey("Deal", deal.id);
    Entity dealEntity;
    try {
      dealEntity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    if (deal.description != null) {
      dealEntity.setProperty("description", deal.description);
      System.out.println(deal.description);
    }
    if (deal.photoBlobkey != null) {
      dealEntity.setProperty("photoBlobkey", deal.photoBlobkey);
    }
    if (deal.start != null) {
      dealEntity.setProperty("start", deal.start);
    }
    if (deal.end != null) {
      dealEntity.setProperty("end", deal.end);
    }
    if (deal.source != null) {
      dealEntity.setProperty("source", deal.source);
    }
    if (deal.restaurantId != -1) {
      dealEntity.setProperty("restaurantId", deal.restaurantId);
    }
    datastore.put(dealEntity);
    searchManager.putDeal(deal, new ArrayList<>());
    return readDeal(deal.id);
  }

  @Override
  public List<Deal> readDeals(List<Long> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns a Deal object transformed from a deal entity.
   *
   * @param dealEntity Deal entity.
   * @return a Deal object transformed from the entity.
   */
  private Deal transformEntityToDeal(Entity dealEntity) {
    long id = dealEntity.getKey().getId();
    String description = (String) dealEntity.getProperty("description");
    String photoBlobkey = (String) dealEntity.getProperty("photoBlobkey");
    String start = (String) dealEntity.getProperty("start");
    String end = (String) dealEntity.getProperty("end");
    String source = (String) dealEntity.getProperty("source");
    long posterId = (long) dealEntity.getProperty("posterId");
    long restaurantId = (long) dealEntity.getProperty("restaurantId");
    return new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId);
  }

  @Override
  public List<Deal> getDealsOfRestaurant(long restaurantId) {
    Query query =
        new Query("Deal")
            .setFilter(
                new Query.FilterPredicate(
                    "restaurantId", Query.FilterOperator.EQUAL, restaurantId));
    PreparedQuery pq = datastore.prepare(query);
    Iterable<Entity> entities = pq.asIterable();
    List<Deal> deals = new ArrayList<>();
    for (Entity entity : entities) {
      deals.add(transformEntityToDeal(entity));
    }
    return deals;
  }
}
