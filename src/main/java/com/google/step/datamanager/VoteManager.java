package com.google.step.datamanager;

public interface VoteManager {
  public int getVotes(long dealId);

  public void vote(long userId, long dealId, int dir);
}
