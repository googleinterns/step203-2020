package com.google.step.datamanager;

import java.util.List;

public interface DealVoteManager {
  /** Gets the number of votes for a deal, specified by {@code dealId}. */
  public int getVotes(long dealId);

  /**
   * Updates the number of votes for a deal, specified by {@code dealId} in direction specified by
   * {@code dir}.
   */
  public void updateDealVotes(long dealId, int dir);

  public List<Long> getDealsWithVotes(List<Long> dealIds, int limit);
}
