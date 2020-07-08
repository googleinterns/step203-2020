package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.step.model.Deal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;
  private final DealTagManager dealTagManager;
  private final FollowManager followManager;
  private final DealSearchManager searchManager;
  private final VoteManager voteManager;

  private final String FOLLOWER_FIELD_NAME = "follower";
  private final String RESTAURANT_FIELD_NAME = "restaurant";
  private final String USER_FIELD_NAME = "user";
  private final String TAG_FIELD_NAME = "tag";

  public DealManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    dealTagManager = new DealTagManagerDatastore();
    followManager = new FollowManagerDatastore();
    searchManager = new DealSearchManagerIndex();
    voteManager = new VoteManagerDatastore();
  }

  public DealManagerDatastore(
      FollowManager followManager,
      DealTagManager dealTagManager,
      VoteManager voteManager,
      DealSearchManager searchManager) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.followManager = followManager;
    this.dealTagManager = dealTagManager;
    this.voteManager = voteManager;
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

  /** Retrieves deals posted by _ followed by user */
  private List<Deal> getDealsPublishedByFollowedRestaurantsOrUsers(
      long userId, String fieldName, String filterAttribute) {
    List<Deal> dealResults = new ArrayList<>();
    List<Long> idsOfFollowedFieldName = followManager.getFollowedSomething(userId, fieldName);
    for (Long id : idsOfFollowedFieldName) {
      Filter propertyFilter = new FilterPredicate(filterAttribute, FilterOperator.EQUAL, id);
      Query query = new Query("Deal").setFilter(propertyFilter);
      PreparedQuery pq = datastore.prepare(query);
      for (Entity entity : pq.asIterable()) {
        dealResults.add(readDeal(entity.getKey().getId()));
      }
    }
    return dealResults;
  }

  /** Retrieves deals posted by users followed by user */
  @Override
  public List<Deal> getDealsPublishedByFollowedUsers(long userId) {
    return getDealsPublishedByFollowedRestaurantsOrUsers(userId, USER_FIELD_NAME, "posterId");
  }

  /** Retrieves deals posted by restaurants followed by user */
  @Override
  public List<Deal> getDealsPublishedByFollowedRestaurants(long userId) {
    return getDealsPublishedByFollowedRestaurantsOrUsers(
        userId, RESTAURANT_FIELD_NAME, "restaurantId");
  }

  /** Retrieves deals posted by tags followed by user */
  //
  @Override
  public List<Deal> getDealsPublishedByFollowedTags(long userId) {
    List<Deal> dealResults = new ArrayList<>();
    List<Long> dealIdResults = new ArrayList<>();
    List<Long> idsOfFollowedTags = followManager.getFollowedTagIds(userId);
    for (Long id : idsOfFollowedTags) {
      List<Long> dealIdsWithTag = dealTagManager.getDealIdsWithTag(id);
      dealIdResults.addAll(dealIdsWithTag);
    }
    // Get rid of duplicate dealID (Deals with multiple tags)
    Set<Long> dealIdSet = new HashSet<>(dealIdResults);
    for (Long dealId : dealIdSet) {
      dealResults.add(readDeal(dealId));
    }
    return dealResults;
  }

  @Override
  public List<Deal> sortDealsBasedOnVotes(List<Deal> deals) {
    Collections.sort(
        deals,
        new Comparator<Deal>() {
          @Override
          public int compare(Deal deal1, Deal deal2) {
            return voteManager.getVotes(deal2.id) - voteManager.getVotes(deal1.id); // Descending
          }
        });
    return deals;
  }
}
