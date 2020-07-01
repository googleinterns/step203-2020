package com.google.step.datamanager;

import com.google.step.model.Deal;
import java.util.List;

public interface DealSearchManager {

  /**
   * Search for Deal which has all the words in query and has all the tags given.
   *
   * @return list of Deal IDs
   */
  public List<Long> search(String query, List<Long> tagIds);

  /** Adds a Deal to the search index */
  public void addDeal(Deal deal, List<Long> tagIds);

  /**
   * Removes a Deal from the search index
   *
   * @param id Deal's ID
   */
  public void removeDeal(long id);
}
