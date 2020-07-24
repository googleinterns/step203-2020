package com.google.step.datamanager;

public class VoteWithExpiry {
  public final int votes;
  public final boolean isExpired;

  public VoteWithExpiry(int votes, boolean isExpired) {
    this.votes = votes;
    this.isExpired = isExpired;
  }
}
