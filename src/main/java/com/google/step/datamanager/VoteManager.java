package com.google.step.datamanager;

public interface VoteManager {
  /** Gets the number of votes for a deal, specified by dealId. */
  public int getVotes(long dealId);

  /**
   * Registers a vote from a user (specified by {@code userId}) for a deal (specified by {@code
   * dealId}) in {@code dir} direction.
   */
  public void vote(long userId, long dealId, int dir);
}
