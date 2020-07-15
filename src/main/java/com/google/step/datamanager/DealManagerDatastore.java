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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.step.model.Deal;
import com.google.step.model.Tag;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DealManagerDatastore implements DealManager {

  private final DatastoreService datastore;
  private final DealTagManager dealTagManager;
  private final DealSearchManager searchManager;
  private final VoteManager voteManager;
  private final TagManager tagManager;

  private final String FOLLOWER_FIELD_NAME = "follower";
  private final String RESTAURANT_FIELD_NAME = "restaurant";
  private final String USER_FIELD_NAME = "user";
  private final String TAG_FIELD_NAME = "tag";

  private final Long OLDEST_DEAL_TIMESTAMP = 1594652120L; // arbitrary datetime of first deal posted
  private final String LOCATION = "Asia/Singapore";

  public DealManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    searchManager = new DealSearchManagerIndex();
    voteManager = new VoteManagerDatastore();
    tagManager = new TagManagerDatastore();
    dealTagManager = new DealTagManagerDatastore();
  }

  public DealManagerDatastore(
      DealTagManager dealTagManager, VoteManager voteManager, DealSearchManager searchManager) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.dealTagManager = dealTagManager;
    this.voteManager = voteManager;
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

  /** Retrieves deals posted by _ followed by user */
  private List<Deal> getDealsPublishedByFollowedRestaurantsOrUsers(
      List<Long> idsOfFollowedFieldName, String filterAttribute) {
    List<Deal> dealResults = new ArrayList<>();
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
  public List<Deal> getDealsPublishedByFollowedUsers(List<Long> userIds) {
    return getDealsPublishedByFollowedRestaurantsOrUsers(userIds, "posterId");
  }

  /** Retrieves deals posted by restaurants followed by user */
  @Override
  public List<Deal> getDealsPublishedByFollowedRestaurants(List<Long> restaurantIds) {
    return getDealsPublishedByFollowedRestaurantsOrUsers(restaurantIds, "restaurantId");
  }

  /** Retrieves deals posted by tags followed by user */
  @Override
  public List<Deal> getDealsPublishedByFollowedTags(List<Long> tagIds) {
    List<Long> dealIdResults = new ArrayList<>();
    for (Long id : tagIds) {
      List<Long> dealIdsWithTag = dealTagManager.getDealIdsWithTag(id);
      dealIdResults.addAll(dealIdsWithTag);
    }
    // Get rid of duplicate dealID (Deals with multiple tags)
    List<Long> dealsWithoutDuplicates = new ArrayList<>(new HashSet<>(dealIdResults));
    return readDeals(dealsWithoutDuplicates);
  }

  /** Sorts deals based on votes (Highest to lowest) */
  @Override
  public List<Deal> sortDealsBasedOnVotes(List<Deal> deals) {
    List<Map<String, Object>> dealWithVotesMaps = new ArrayList<Map<String, Object>>();
    // Creates a list of maps with votes as an attribute to be sorted
    for (Deal deal : deals) {
      Map<String, Object> dealWithVotesMap = new HashMap<>();
      dealWithVotesMap.put("votes", voteManager.getVotes(deal.id));
      dealWithVotesMap.put("deal", deal);
      dealWithVotesMaps.add(dealWithVotesMap);
    }
    return sortDealMapsBasedOnValue(dealWithVotesMaps, "votes");
  }

  /** Method to sort a list of maps based on a value and return a list of deals */
  private List<Deal> sortDealMapsBasedOnValue(
      List<Map<String, Object>> dealMaps, String attribute) {
    Collections.sort(
        dealMaps,
        new Comparator<Map<String, Object>>() {
          @Override
          public int compare(Map<String, Object> deal1, Map<String, Object> deal2) {
            if (attribute.equals("hotScore")) // comparing hot score (double values)
            return -Double.compare(
                  (double) deal1.get(attribute), (double) deal2.get(attribute)); // Descending
            else // Comparing votes
            return (int) deal2.get(attribute) - (int) deal1.get(attribute); // Descending
          }
        });
    List<Deal> dealResults = new ArrayList<>(); // creating list of deals
    for (Map<String, Object> dealMap : dealMaps) {
      dealResults.add((Deal) dealMap.get("deal"));
    }
    return dealResults;
  }

  /** Sorts deals based on new (Newest to oldest) */
  @Override
  public List<Deal> sortDealsBasedOnNew(List<Deal> deals) {
    Collections.sort(
        deals,
        new Comparator<Deal>() {
          @Override
          public int compare(Deal deal1, Deal deal2) {
            return LocalDateTime.parse(deal2.creationTimeStamp)
                .compareTo(LocalDateTime.parse(deal1.creationTimeStamp)); // Descending
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
    return sign * order + seconds / 45000;
  }

  /** Gets a list of trending deals based on the hot score */
  @Override
  public List<Deal> getTrendingDeals() {
    Query query = new Query("Deal");
    PreparedQuery pq = datastore.prepare(query);
    List<Deal> dealResults = new ArrayList<>();
    List<Map<String, Object>> dealWithHotScoreMaps = new ArrayList<Map<String, Object>>();
    for (Entity entity : pq.asIterable()) {
      Map<String, Object> dealWithHotScoreMap = new HashMap<>();
      dealWithHotScoreMap.put("hotScore", calculateHotScore(entity));
      dealWithHotScoreMap.put("deal", transformEntityToDeal(entity));
      dealWithHotScoreMaps.add(dealWithHotScoreMap);
    }
    return sortDealMapsBasedOnValue(dealWithHotScoreMaps, "hotScore");
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
}
