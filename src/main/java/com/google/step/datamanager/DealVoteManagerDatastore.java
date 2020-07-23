package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;

public class DealVoteManagerDatastore implements DealVoteManager {

  private final DatastoreService datastore;

  public DealVoteManagerDatastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public int getVotes(long dealId) {
    Filter dealFilter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Query query = new Query("DealVote").setFilter(dealFilter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();
    if (entity == null) {
      return 0;
    }
    int votes = (int) (long) entity.getProperty("votes");
    return votes;
  }

  @Override
  public List<Long> getDealsWithVotes(List<Long> dealIds, int limit) {
    List<Long> dealIdsResults = new ArrayList<>();
    Filter propertyFilter = new FilterPredicate("deal", FilterOperator.IN, dealIds);
    Query query =
        new Query("DealVote").setFilter(propertyFilter).addSort("votes", SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(query);
    Iterable<Entity> entities = null;
    if (limit > 0) {
      entities = pq.asIterable(FetchOptions.Builder.withLimit(limit));
    } else {
      entities = pq.asIterable();
    }
    for (Entity entity : entities) {
      dealIdsResults.add((long) entity.getProperty("deal"));
    }
    return dealIdsResults;
    // Add those that have not been voted to the end.
  }

  @Override
  public void updateDealVotes(long dealId, int dir) {
    Filter filter = new FilterPredicate("deal", FilterOperator.EQUAL, dealId);
    Query query = new Query("DealVote").setFilter(filter);
    PreparedQuery pq = datastore.prepare(query);
    Entity entity = pq.asSingleEntity();

    // If the deal has not been voted before
    if (entity == null) {
      entity = new Entity("DealVote");
      entity.setProperty("deal", dealId);
      entity.setProperty("votes", dir);
    } else {
      int votes = (int) (long) entity.getProperty("votes");
      entity.setProperty("votes", votes + dir);
    }
    datastore.put(entity);
  }
}
