package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class VoteCache {
  // time the vote can live for in seconds
  private final long TIME_TO_LIVE = 3600;

  private final DatastoreService datastore;

  public VoteCache() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  public VoteWithExpiry readVotes(long dealId) {
    Key key = KeyFactory.createKey("VoteCache", dealId);
    Entity entity;
    try {
      entity = datastore.get(key);
    } catch (EntityNotFoundException e) {
      return new VoteWithExpiry(0, true);
    }

    int votes = ((Long) entity.getProperty("votes")).intValue();
    long expiryTime = (long) entity.getProperty("expiry");
    boolean isExpired = System.currentTimeMillis() / 1000 > expiryTime;

    return new VoteWithExpiry(votes, isExpired);
  }

  public void saveVotes(long dealId, int votes) {
    Entity entity = new Entity("VoteCache", dealId);
    entity.setProperty("votes", votes);
    long expiryTime = System.currentTimeMillis() / 1000 + TIME_TO_LIVE;
    entity.setProperty("expiry", expiryTime);

    datastore.put(entity);
  }
}
