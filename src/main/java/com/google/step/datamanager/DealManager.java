package com.google.step.datamanager;

import com.google.step.model.Deal;

public interface DealManager {
  public Deal createDeal(
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long poster,
      long restaurant);

  public Deal readDeal(long id);

  public Deal updateDeal(Deal deal);

  public void deleteDeal(long id);
}
