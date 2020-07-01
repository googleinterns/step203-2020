package com.google.step.datamanager;

import com.google.step.model.Deal;
import java.util.List;

public interface DealSearchManager {

  public List<Long> search(String query, List<Long> tagIds);

  public void addDeal(Deal deal, List<Long> tagIds);

  public void removeDeal(long id);
}
