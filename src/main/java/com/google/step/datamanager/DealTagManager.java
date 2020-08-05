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
   * Returns ids of deals that have tag with the id.
   *
   * @param id id of the tag
   * @return ids of deals that have tag with the id.
   */
  public List<Long> getDealIdsWithTag(long id);

  /**
   * Updates tags of deal with the id to be the list of tags.
   *
   * @param dealId id of the deal.
   * @param tagIds updated tag ids of the deal.
   */
  public void updateTagsOfDeal(long dealId, List<Long> tagIds);

  /**
   * Deletes all tags of the deal with given id
   *
   * @param dealId id of the deal
   */
  public void deleteAllTagsOfDeal(long dealId);
}
