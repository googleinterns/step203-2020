package com.google.step.datamanager;

public class VoteWithExpiry {
  public final long votes;
  public final boolean isExpired;

  public VoteWithExpiry(long votes, boolean isExpired) {
    this.votes = votes;
    this.isExpired = isExpired;
  }
}
