package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.step.model.Deal;
import java.util.List;
import java.util.stream.Collectors;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;
  private final DealSearchManager searchManager;
  private final TagManager tagManager;
  private final DealTagManager dealTagManager;

  public DealManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    searchManager = new DealSearchManagerIndex();
    tagManager = new TagManagerDatastore();
    dealTagManager = new DealTagManagerDatastore();
  }

  public DealManagerDatastore(DealSearchManager searchManager) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.searchManager = searchManager;
  }

  @Override
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId,
      List<String> tagNames) {
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

    // gets the tag IDs from the tag names
    List<Long> tagIds = getTagIdsFromNames(tagNames);

    dealTagManager.updateTagsOfDeal(id, tagIds);

    Deal deal = new Deal(id, description, photoBlobkey, start, end, source, posterId, restaurantId);
    searchManager.putDeal(deal, tagIds);

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
  public Deal updateDeal(Deal deal, List<String> tagNames) {
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
    if (tagNames != null) {
      List<Long> tagIds = getTagIdsFromNames(tagNames);
      dealTagManager.updateTagsOfDeal(deal.id, tagIds);
    }
    datastore.put(dealEntity);
    searchManager.putDeal(deal, dealTagManager.getTagIdsOfDeal(deal.id));
    return readDeal(deal.id);
  }

  private List<Long> getTagIdsFromNames(List<String> tagNames) {
    return tagNames.stream()
        .map(tagName -> tagManager.readOrCreateTagByName(tagName))
        .map(tag -> tag.id)
        .collect(Collectors.toList());
  }

  @Override
  public List<Deal> readDeals(List<Long> ids) {
    // TODO Auto-generated method stub
    return null;
  }
}
