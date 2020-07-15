package com.google.step.datamanager;

import com.google.step.model.Deal;
<<<<<<< HEAD
=======
import com.google.step.model.Tag;
>>>>>>> origin/master
import java.util.List;

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

  public List<Deal> getTrendingDeals();

  public List<Deal> getDealsPublishedByFollowedUsers(List<Long> userIds);

  public List<Deal> getDealsPublishedByFollowedRestaurants(List<Long> restaurantIds);

  public List<Deal> getDealsPublishedByFollowedTags(List<Long> tagIds);

  public List<Deal> sortDealsBasedOnVotes(List<Deal> deals);

  public List<Deal> sortDealsBasedOnNew(List<Deal> deals);
  
  public List<Tag> getTags(long dealId);

  public List<Deal> readDeals(List<Long> ids);
}
