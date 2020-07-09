package com.google.step.datamanager;

import com.google.step.model.Deal;
import java.util.List;

public interface DealManager {
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId);

  public Deal readDeal(long id);

  public Deal updateDeal(Deal deal);

  public void deleteDeal(long id);

  /** Gets trending deals based on an integrated criteria of votes, comments, freshness */
  public List<Deal> getTrendingDeals();

  public List<Deal> getDealsPublishedByFollowedUsers(long userId);

  public List<Deal> getDealsPublishedByFollowedRestaurants(long userId);

  public List<Deal> getDealsPublishedByFollowedTags(long userId);

  public List<Deal> sortDealsBasedOnVotes(List<Deal> deals);

  public List<Deal> sortDealsBasedOnNew(List<Deal> deals);
}
