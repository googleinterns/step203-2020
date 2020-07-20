package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.step.model.Deal;
import com.google.step.model.Tag;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;
  private final DealSearchManager searchManager;
  private final String LOCATION = "Asia/Singapore";
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
    tagManager = new TagManagerDatastore();
    dealTagManager = new DealTagManagerDatastore();
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
    String creationTimeStamp = LocalDateTime.now(ZoneId.of(LOCATION)).toString();
    entity.setProperty("timestamp", creationTimeStamp);

    Key key = datastore.put(entity);
    long id = key.getId();

    // gets the tag IDs from the tag names
    List<Long> tagIds = getTagIdsFromNames(tagNames);

    dealTagManager.updateTagsOfDeal(id, tagIds);

    Deal deal =
        new Deal(
            id,
            description,
            photoBlobkey,
            start,
            end,
            source,
            posterId,
            restaurantId,
            creationTimeStamp);
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
    return transformEntityToDeal(dealEntity);
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
    return transformEntityToDeal(dealEntity);
  }

  /** Retrieves deals posted by users */
  @Override
  public List<Deal> getDealsPublishedByUsers(List<Long> userIds) {
    return new ArrayList<Deal>();
  }

  /** Retrieves deals posted by restaurants */
  @Override
  public List<Deal> getDealsPublishedByRestaurants(List<Long> restaurantIds) {
    return new ArrayList<Deal>();
  }

  @Override
  public List<Deal> getAllDeals() {
    return new ArrayList<Deal>();
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
    String creationTimeStamp = (String) dealEntity.getProperty("timestamp");
    return new Deal(
        id,
        description,
        photoBlobkey,
        start,
        end,
        source,
        posterId,
        restaurantId,
        creationTimeStamp);
  }

  @Override
  public List<Tag> getTags(long dealId) {
    List<Long> tagIds = dealTagManager.getTagIdsOfDeal(dealId);
    return tagManager.readTags(tagIds);
  }

  private List<Long> getTagIdsFromNames(List<String> tagNames) {
    return tagNames.stream()
        .map(tagName -> tagManager.readOrCreateTagByName(tagName))
        .map(tag -> tag.id)
        .collect(Collectors.toList());
  }

  @Override
  public List<Deal> readDeals(List<Long> ids) {
    List<Key> keys =
        ids.stream().map(id -> KeyFactory.createKey("Deal", id)).collect(Collectors.toList());
    Collection<Entity> dealEntities;
    try {
      dealEntities = datastore.get(keys).values();
    } catch (IllegalArgumentException | DatastoreFailureException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    List<Deal> deals =
        dealEntities.stream()
            .map(entity -> transformEntityToDeal(entity))
            .collect(Collectors.toList());
    return deals;
  }

  @Override
  public List<Deal> getDealsPublishedByUser(long userId) {
    Query query =
        new Query("Deal")
            .setFilter(new Query.FilterPredicate("posterId", Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query);
    Iterable<Entity> dealEntities = results.asIterable();
    List<Deal> deals = new ArrayList<>();
    for (Entity entity : dealEntities) {
      deals.add(transformEntityToDeal(entity));
    }
    return deals;
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
