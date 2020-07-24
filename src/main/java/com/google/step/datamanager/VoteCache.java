package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class VoteCache {
  // time the vote can live for in seconds
  private final long TIME_TO_LIVE = 3600;

  private final DatastoreService datastore;

  public VoteCache() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public VoteWithExpiry readVotes(long dealId) {
    Query query =
        new Query("VoteCache")
            .setFilter(new Query.FilterPredicate("dealId", Query.FilterOperator.EQUAL, dealId));

    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    if (entity == null) {
      return new VoteWithExpiry(0, true);
    }

    long votes = (long) entity.getProperty("votes");
    long expiryTime = (long) entity.getProperty("expiry");
    boolean isExpired = System.currentTimeMillis() / 1000 > expiryTime;

    return new VoteWithExpiry(votes, isExpired);
  }

  public void saveVotes(long dealId, long votes) {
    Entity entity = new Entity("VoteCache");
    entity.setProperty("dealId", dealId);
    entity.setProperty("votes", votes);
    long expiryTime = System.currentTimeMillis() / 1000 + TIME_TO_LIVE;
    entity.setProperty("expiry", expiryTime);

    datastore.put(entity);
  }
}
