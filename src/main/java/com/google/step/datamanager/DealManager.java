package com.google.step.datamanager;

import com.google.step.model.Deal;
import com.google.step.model.Tag;
import java.util.List;
import java.util.Set;

public interface DealManager {
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId,
      List<String> tagNames);

  public Deal readDeal(long id);

  public Deal updateDeal(Deal deal, List<String> tagNames);

  public void deleteDeal(long id);

  public List<Long> getDealsPublishedByUsers(Set<Long> userIds, int limit);

  public List<Long> getDealsPublishedByUsersSortByNew(Set<Long> userIds, int limit);

  public List<Long> getDealsPublishedByRestaurants(Set<Long> restaurantIds, int limit);

  public List<Long> getDealsPublishedByRestaurantsSortByNew(Set<Long> restaurantIds, int limit);

  // Returns a list of deals with the deal ids matching the ids in the set, sorted by new
  public List<Long> getDealsWithIdsSortByNew(Set<Long> ids, int limit);

  // Returns a list of deals with the deal ids matching the ids in the set
  public List<Long> getDealsWithIds(Set<Long> ids, int limit);

  public List<Deal> getAllDeals();

  public List<Tag> getTags(long dealId);

  public List<Deal> readDeals(List<Long> ids);

  // Returns a list of deals in the order of the ids given
  public List<Deal> readDealsOrder(List<Long> ids);

  public List<Deal> getDealsOfRestaurant(long restaurantId);

  public List<Deal> getDealsPublishedByUser(long userId);
}
