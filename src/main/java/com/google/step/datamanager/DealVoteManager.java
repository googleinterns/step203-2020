package com.google.step.datamanager;

public interface DealVoteManager {
  /** Gets the number of votes for a deal, specified by {@code dealId}. */
  public int getVotes(long dealId);

  /** Gets the number of votes for a deal, specified by {@code dealId}. */
  public void updateDealVotes(long dealId, int dir);
}
