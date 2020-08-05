package com.google.step.datamanager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;

public class DealVoteCountManagerDatastore implements DealVoteCountManager {

  private final DatastoreService datastore;

  public DealVoteCountManagerDatastore() {
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
  public List<Long> sortDealsInOrderOfVotes(List<Long> dealIds) {
    List<Long> dealIdResults = new ArrayList<>();
    List<Long> dealIdsArrayList = new ArrayList<>(dealIds);
    if (dealIds.size() > 0) {
      Filter dealFilter = new FilterPredicate("deal", FilterOperator.IN, dealIds);
      Query query =
          new Query("DealVote").setFilter(dealFilter).addSort("votes", SortDirection.DESCENDING);
      PreparedQuery pq = datastore.prepare(query);
      Iterable<Entity> entities = pq.asIterable();
      for (Entity entity : entities) {
        dealIdsArrayList.remove(new Long((long) entity.getProperty("deal")));
        dealIdResults.add((long) entity.getProperty("deal"));
      }
    }
    // Add those that have not been voted on and not in datastore to the end
    dealIdResults.addAll(dealIdsArrayList);
    return dealIdResults;
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
