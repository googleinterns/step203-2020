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
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.step.model.Deal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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

  private final Long OLDEST_DEAL_TIMESTAMP = 1594652120L; // arbitrary datetime of first deal posted
  private final String LOCATION = "Asia/Singapore";

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
    String timestamp = LocalDateTime.now(ZoneId.of("Asia/Singapore")).toString();
    entity.setProperty("timestamp", timestamp);

    Key key = datastore.put(entity);
    long id = key.getId();

    Deal deal =
        new Deal(
            id, description, photoBlobkey, start, end, source, posterId, restaurantId, timestamp);
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
    return transformEntitytoDeal(dealEntity);
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
    return transformEntitytoDeal(dealEntity);
  }

  /**
   * Returns a Deal object transformed from a deal entity.
   *
   * @param dealEntity Deal entity.
   * @return a Deal object transformed from the entity.
   */
  private Deal transformEntitytoDeal(Entity dealEntity) {
    long id = dealEntity.getKey().getId();
    String description = (String) dealEntity.getProperty("description");
    String photoBlobkey = (String) dealEntity.getProperty("photoBlobkey");
    String start = (String) dealEntity.getProperty("start");
    String end = (String) dealEntity.getProperty("end");
    String source = (String) dealEntity.getProperty("source");
    long posterId = (long) dealEntity.getProperty("posterId");
    long restaurantId = (long) dealEntity.getProperty("restaurantId");
    String timestamp = (String) dealEntity.getProperty("timestamp");
    return new Deal(
        id, description, photoBlobkey, start, end, source, posterId, restaurantId, timestamp);
  }

  /** Retrieves deals posted by _ followed by user */
  private List<Deal> getDealsPublishedByFollowedRestaurantsOrUsers(
      long userId, String fieldName, String filterAttribute) {
    List<Deal> dealResults = new ArrayList<>();
    List<Long> idsOfFollowedFieldName = getFollowedSomething(userId, fieldName);
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

  /**
   * This is copied from the follow manager method which is private, would it be better to make the
   * original method public?
   */
  private List<Long> getFollowedSomething(long followerId, String fieldName) {
    Filter userFilter = new FilterPredicate(FOLLOWER_FIELD_NAME, FilterOperator.EQUAL, followerId);
    Filter otherFilter = new FilterPredicate(fieldName, FilterOperator.NOT_EQUAL, null);
    Filter filter = CompositeFilterOperator.and(userFilter, otherFilter);
    Query query = new Query("Follow").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);

    List<Long> list = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      list.add((Long) entity.getProperty(fieldName));
    }
    return list;
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
    List<Long> dealsWithoutDuplicates = new ArrayList<>(new HashSet<>(dealIdResults));
    for (Long dealId : dealsWithoutDuplicates) {
      dealResults.add(readDeal(dealId));
    }
    return dealResults;
  }

  /** Sorts deals based on votes (Highest to lowest) */
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

  /** Sorts deals based on new (Newest to oldest) */
  @Override
  public List<Deal> sortDealsBasedOnNew(List<Deal> deals) {
    Collections.sort(
        deals,
        new Comparator<Deal>() {
          @Override
          public int compare(Deal deal1, Deal deal2) {
            return LocalDateTime.parse(deal2.timestamp)
                .compareTo(LocalDateTime.parse(deal1.timestamp)); // Descending
          }
        });
    return deals;
  }

  /** Sorts deals based on hot score (Highest to lowest) */
  private List<Deal> sortDealsBasedOnHotScore(List<Deal> deals) {
    Collections.sort(
        deals,
        new Comparator<Deal>() {
          @Override
          public int compare(Deal deal1, Deal deal2) {
            return -Double.compare(deal1.getHotScore(), deal2.getHotScore()); // Descending
          }
        });
    return deals;
  }

  /** Sorts deals based on hot score (Highest to lowest) */
  private double epochSeconds(String timestamp) {
    LocalDateTime time = LocalDateTime.parse(timestamp);
    long epoch = time.atZone(ZoneId.of(LOCATION)).toEpochSecond();
    return epoch;
  }

  /**
   * Calculates a hot score for each deal entity, which takes into account both the time and the
   * amount of votes it got
   */
  private double calculateHotScore(Entity dealEntity) {
    int netVotes = voteManager.getVotes(dealEntity.getKey().getId());
    double order = Math.log(Math.max(Math.abs(netVotes), 1));
    int sign = 0;
    if (netVotes > 0) sign = 1;
    else if (netVotes < 0) sign = -1;
    double seconds =
        epochSeconds((String) dealEntity.getProperty("timestamp")) - OLDEST_DEAL_TIMESTAMP;
    System.out.println(sign * order + seconds / 45000);
    return sign * order + seconds / 45000;
  }

  /** Gets a list of trending deals based on the hot score */
  @Override
  public List<Deal> getTrendingDeals() {
    Query query = new Query("Deal");
    PreparedQuery pq = datastore.prepare(query);
    List<Deal> dealResults = new ArrayList<>();
    for (Entity entity : pq.asIterable()) {
      double hotScore = calculateHotScore(entity);
      Deal deal = transformEntitytoDeal(entity);
      deal.setHotScore(hotScore);
      dealResults.add(deal);
    }
    return sortDealsBasedOnHotScore(dealResults);
  }
}
