package com.google.step.datamanager;

import java.util.List;

/** DealTagManager handles deal tag relations. */
public interface DealTagManager {
  /**
   * Returns tag ids of the deal with the id.
   *
   * @param id id of the deal.
   * @return tag ids of the deal with the id.
   */
  public List<Long> getTagIdsOfDeal(long id);

  /**
   * Updates tags of deal with the id to be the list of tags.
   *
   * @param dealId id of the deal.
   * @param tagIds updated tag ids of the deal.
   */
  public void updateTagsOfDeal(long dealId, List<Long> tagIds);
}
