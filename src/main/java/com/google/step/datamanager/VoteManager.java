package com.google.step.datamanager;

public interface VoteManager {
  /** Gets the number of votes the Deal with the given dealId has */
  public int getVotes(long dealId);

  /**
   * The user with the given userId votes on the Deal with the given dealId in the given direction.
   */
  public void vote(long userId, long dealId, int dir);
}
