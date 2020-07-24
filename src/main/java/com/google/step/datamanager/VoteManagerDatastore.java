package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class VoteManagerDatastore implements VoteManager {

  private final DatastoreService datastore;
  private final VoteCache voteCache;

  public VoteManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    voteCache = new VoteCache();
  }

  public VoteManagerDatastore(VoteCache voteCache) {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.voteCache = voteCache;
  }

  private int computeVotes(long dealId) {
    Filter dealFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Query query = new Query("Vote").setFilter(dealFilter);
    PreparedQuery pq = datastore.prepare(query);

    int totalVotes = 0;
    for (Entity entity : pq.asIterable()) {
      int dir = ((Long) entity.getProperty("dir")).intValue();
      totalVotes += dir;
    }
    return totalVotes;
  }

  @Override
  public int getVotes(long dealId) {
    VoteWithExpiry voteWithExpiry = voteCache.readVotes(dealId);
    int votes = voteWithExpiry.votes;
    if (voteWithExpiry.isExpired) {
      votes = computeVotes(dealId);
      voteCache.saveVotes(dealId, votes);
    }
    return votes;
  }

  @Override
  public void vote(long userId, long dealId, int dir) {
    // check if this user has voted on this deal before
    Filter userFilter = new FilterPredicate("user", FilterOperator.EQUAL, userId);
    Filter dealFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Filter filter = CompositeFilterOperator.and(userFilter, dealFilter);
    Query query = new Query("Vote").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    // If entity exists, use it. Else, create a new entity.
    if (entity == null) {
      entity = new Entity("Vote");
      entity.setProperty("user", userId);
      entity.setProperty("deal", dealId);
    }

    entity.setProperty("dir", dir);
    datastore.put(entity);

    // delete old cache
    voteCache.deleteCache(dealId);
  }

  @Override
  public int getDirection(long userId, long dealId) {
    // check if this user has voted on this deal before
    Filter userFilter = new FilterPredicate("user", FilterOperator.EQUAL, userId);
    Filter dealFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Filter filter = CompositeFilterOperator.and(userFilter, dealFilter);
    Query query = new Query("Vote").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    // If user have not voted before return 0. Else, return the value of the entity.
    if (entity == null) {
      return 0;
    }
    return ((Long) entity.getProperty("dir")).intValue();
  }
}
