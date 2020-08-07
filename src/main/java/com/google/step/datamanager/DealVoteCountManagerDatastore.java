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
import java.util.stream.Collectors;

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
  public List<Long> sortDealsInOrderOfVotes(List<Long> dealIds, int limit) {
    List<Long> dealIdResults = new ArrayList<>();
    List<Long> dealIdsArrayList = new ArrayList<>(dealIds);
    if (dealIds.size() > 0) {
      Filter dealFilter = new FilterPredicate("deal", FilterOperator.IN, dealIds);
      Query query =
          new Query("DealVote").setFilter(dealFilter).addSort("votes", SortDirection.DESCENDING);
      PreparedQuery pq = datastore.prepare(query);
      // Represents the index where the vote count is 0
      int indexWithZeroVotes = 0;
      for (Entity entity : pq.asIterable()) {
        if ((int) (long) entity.getProperty("votes") > 0) {
          indexWithZeroVotes += 1;
        }
        // Removes the deals that have been voted on
        dealIdsArrayList.remove(new Long((long) entity.getProperty("deal")));
        dealIdResults.add((long) entity.getProperty("deal"));
      }
      // dealIdArrayList contains the unvoted deals that wont be in the database, and these deals
      // should be treated as having a vote count of 0
      dealIdResults.addAll(indexWithZeroVotes, dealIdsArrayList);
      // If limit == -1, return all dealIds
      dealIdResults =
          limit == -1
              ? dealIdResults
              : dealIdResults.stream().limit(limit).collect(Collectors.toList());
    }
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
