package com.google.step.datamanager;

import com.google.step.model.Deal;
import com.google.step.model.Tag;
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

  public List<Tag> getTags(long dealId);

  public List<Deal> readDeals(List<Long> ids);

  public List<Deal> getDealsPublishedByUser(long userId);
}
