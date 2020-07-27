package com.google.step.datamanager;

import java.util.List;

public interface DealVoteCountManager {
  /** Gets the number of votes for a deal, specified by {@code dealId}. */
  public int getVotes(long dealId);

  /** Updates the number of votes for a deal, specified by dealId and dir. */
  public void updateDealVotes(long dealId, int dir);

  /** Retrieves deals in order of votes */
  public List<Long> getDealsInOrderOfVotes(List<Long> dealIds, int limit);
}
